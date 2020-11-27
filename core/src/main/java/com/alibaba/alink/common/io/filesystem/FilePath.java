package com.alibaba.alink.common.io.filesystem;

import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.core.fs.Path;
import org.apache.flink.ml.api.misc.param.Params;
import org.apache.flink.util.FileUtils;
import org.apache.flink.util.Preconditions;

import com.alibaba.alink.common.io.annotations.AnnotationUtils;
import com.alibaba.alink.common.utils.JsonConverter;
import com.alibaba.alink.operator.common.io.reader.HttpFileSplitReader;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

public final class FilePath implements Serializable {

	private static final long serialVersionUID = -4190125972918364361L;

	private final Path path;
	private BaseFileSystem <?> fileSystem;

	public FilePath(String path) {
		this(path, null);
	}

	public FilePath(String path, BaseFileSystem <?> fileSystem) {
		this(new Path(path), fileSystem);
	}

	public FilePath(Path path) {
		this(path, null);
	}

	public FilePath(Path path, BaseFileSystem <?> fileSystem) {
		this.path = path;
		this.fileSystem = fileSystem;

		init();
	}

	public Path getPath() {
		return path;
	}

	public String getPathStr() {
		return path.toString();
	}

	public BaseFileSystem <?> getFileSystem() {
		return fileSystem;
	}

	public String serialize() {
		return JsonConverter.toJson(FilePathJsonable.fromFilePath(this));
	}

	public static FilePath deserialize(String str) {
		if (str == null) {
			return null;
		}

		return str.trim().startsWith("{") ? JsonConverter.fromJson(str, FilePathJsonable.class).toFilePath()
			: new FilePath(str);
	}

	private final static class FilePathJsonable implements Serializable {
		private static final long serialVersionUID = -7977816756958660145L;

		public String path;
		public Params params;

		// for json serialize.
		public FilePathJsonable() {
		}

		public FilePathJsonable(String path, Params params) {
			this.path = path;
			this.params = params;
		}

		public FilePath toFilePath() {
			return new FilePath(path, params == null ? null : BaseFileSystem.of(params));
		}

		public static FilePathJsonable fromFilePath(FilePath filePath) {
			return new FilePathJsonable(filePath.getPathStr(),
				filePath.getFileSystem() == null ? null : filePath.getFileSystem().getParams());
		}
	}

	public static String download(FilePath folder, String fileName) throws IOException {
		// local
		if (folder.getFileSystem() instanceof LocalFileSystem) {
			return folder.getPathStr();
		}

		File localConfDir = new File(System.getProperty("java.io.tmpdir"), FileUtils.getRandomFilename(""));
		String scheme = folder.getPath().toUri().getScheme();

		if (!localConfDir.mkdir()) {
			throw new RuntimeException("Could not create the dir " + localConfDir.getAbsolutePath());
		}

		try (FileOutputStream outputStream = new FileOutputStream(
			Paths.get(localConfDir.getPath(), fileName).toFile())) {
			// http
			if (scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
				try (HttpFileSplitReader reader
						 = new HttpFileSplitReader(new Path(folder.getPath(), fileName).toString())) {

					long fileLen = reader.getFileLength();
					reader.open(null, 0, fileLen);

					int offset = 0;
					byte[] buffer = new byte[1024];

					while (offset < fileLen) {
						int len = reader.read(buffer, offset, 1024);
						outputStream.write(buffer, offset, len);
						offset += len;
					}

				}
			} else {
				// file system
				try (FSDataInputStream inputStream
						 = folder.getFileSystem().open(new Path(folder.getPath(), fileName))) {

					IOUtils.copy(inputStream, outputStream);
				}
			}

			return localConfDir.getAbsolutePath();
		}
	}

	private void init() {
		Preconditions.checkNotNull(path, "Must be set path.");
		String schema = path.toUri().getScheme();

		if (schema != null && (schema.equals("http") || schema.equals("https"))) {
			return;
		}

		if (fileSystem == null) {
			schema = rewriteUri(path.toUri()).getScheme();

			List<String> allFileSystemNames = AnnotationUtils.allFileSystemNames();

			for (String fileSystemName : allFileSystemNames) {
				BaseFileSystem<?> localFileSystem;

				try {
					localFileSystem = AnnotationUtils.createFileSystem(fileSystemName, new Params());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				String fileSystemSchema;

				try {
					fileSystemSchema = localFileSystem.getSchema();
				} catch (Exception ex) {
					continue;
				}

				if (fileSystemSchema != null && fileSystemSchema.equals(schema)) {
					fileSystem = localFileSystem;
					break;
				}
			}

			if (fileSystem == null) {
				throw new IllegalArgumentException(
					String.format("There are not file system matched the %s, "
							+ "Maybe that set the filesystem of %s in file path's constructor will be better.",
						path.toString(), schema
					)
				);
			}
		}
	}

	private static URI rewriteUri(URI fsUri) {
		final URI uri;

		if (fsUri.getScheme() != null) {
			uri = fsUri;
		}
		else {
			// Apply the default fs scheme
			final URI defaultUri = org.apache.flink.core.fs.local.LocalFileSystem.getLocalFsURI();
			URI rewrittenUri = null;

			try {
				rewrittenUri = new URI(defaultUri.getScheme(), null, defaultUri.getHost(),
					defaultUri.getPort(), fsUri.getPath(), null, null);
			}
			catch (URISyntaxException e) {
				// for local URIs, we make one more try to repair the path by making it absolute
				if (defaultUri.getScheme().equals("file")) {
					try {
						rewrittenUri = new URI(
							"file", null,
							new Path(new File(fsUri.getPath()).getAbsolutePath()).toUri().getPath(),
							null);
					} catch (URISyntaxException ignored) {
						// could not help it...
					}
				}
			}

			if (rewrittenUri != null) {
				uri = rewrittenUri;
			}
			else {
				throw new IllegalArgumentException("The file system URI '" + fsUri +
					"' declares no scheme and cannot be interpreted relative to the default file system URI ("
					+ defaultUri + ").");
			}
		}

		// print a helpful pointer for malformed local URIs (happens a lot to new users)
		if (uri.getScheme().equals("file") && uri.getAuthority() != null && !uri.getAuthority().isEmpty()) {
			String supposedUri = "file:///" + uri.getAuthority() + uri.getPath();

			throw new IllegalArgumentException("Found local file path with authority '" + uri.getAuthority() + "' in path '"
				+ uri.toString() + "'. Hint: Did you forget a slash? (correct path would be '" + supposedUri + "')");
		}

		return uri;
	}
}
