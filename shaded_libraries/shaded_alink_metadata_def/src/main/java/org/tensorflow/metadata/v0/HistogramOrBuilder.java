// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: statistics.proto

package org.tensorflow.metadata.v0;

public interface HistogramOrBuilder extends
    // @@protoc_insertion_point(interface_extends:tensorflow.metadata.v0.Histogram)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * The number of NaN values in the dataset.
   * </pre>
   *
   * <code>uint64 num_nan = 1;</code>
   * @return The numNan.
   */
  long getNumNan();

  /**
   * <pre>
   * The number of undefined values in the dataset.
   * </pre>
   *
   * <code>uint64 num_undefined = 2;</code>
   * @return The numUndefined.
   */
  long getNumUndefined();

  /**
   * <pre>
   * A list of buckets in the histogram, sorted from lowest bucket to highest
   * bucket.
   * </pre>
   *
   * <code>repeated .tensorflow.metadata.v0.Histogram.Bucket buckets = 3;</code>
   */
  java.util.List<Histogram.Bucket>
      getBucketsList();
  /**
   * <pre>
   * A list of buckets in the histogram, sorted from lowest bucket to highest
   * bucket.
   * </pre>
   *
   * <code>repeated .tensorflow.metadata.v0.Histogram.Bucket buckets = 3;</code>
   */
  Histogram.Bucket getBuckets(int index);
  /**
   * <pre>
   * A list of buckets in the histogram, sorted from lowest bucket to highest
   * bucket.
   * </pre>
   *
   * <code>repeated .tensorflow.metadata.v0.Histogram.Bucket buckets = 3;</code>
   */
  int getBucketsCount();
  /**
   * <pre>
   * A list of buckets in the histogram, sorted from lowest bucket to highest
   * bucket.
   * </pre>
   *
   * <code>repeated .tensorflow.metadata.v0.Histogram.Bucket buckets = 3;</code>
   */
  java.util.List<? extends Histogram.BucketOrBuilder>
      getBucketsOrBuilderList();
  /**
   * <pre>
   * A list of buckets in the histogram, sorted from lowest bucket to highest
   * bucket.
   * </pre>
   *
   * <code>repeated .tensorflow.metadata.v0.Histogram.Bucket buckets = 3;</code>
   */
  Histogram.BucketOrBuilder getBucketsOrBuilder(
      int index);

  /**
   * <pre>
   * The type of the histogram.
   * </pre>
   *
   * <code>.tensorflow.metadata.v0.Histogram.HistogramType type = 4;</code>
   * @return The enum numeric value on the wire for type.
   */
  int getTypeValue();
  /**
   * <pre>
   * The type of the histogram.
   * </pre>
   *
   * <code>.tensorflow.metadata.v0.Histogram.HistogramType type = 4;</code>
   * @return The type.
   */
  Histogram.HistogramType getType();

  /**
   * <pre>
   * An optional descriptive name of the histogram, to be used for labeling.
   * </pre>
   *
   * <code>string name = 5;</code>
   * @return The name.
   */
  String getName();
  /**
   * <pre>
   * An optional descriptive name of the histogram, to be used for labeling.
   * </pre>
   *
   * <code>string name = 5;</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();
}
