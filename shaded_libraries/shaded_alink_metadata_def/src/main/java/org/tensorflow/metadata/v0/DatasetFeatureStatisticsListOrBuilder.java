// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: statistics.proto

package org.tensorflow.metadata.v0;

public interface DatasetFeatureStatisticsListOrBuilder extends
    // @@protoc_insertion_point(interface_extends:tensorflow.metadata.v0.DatasetFeatureStatisticsList)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .tensorflow.metadata.v0.DatasetFeatureStatistics datasets = 1;</code>
   */
  java.util.List<DatasetFeatureStatistics>
      getDatasetsList();
  /**
   * <code>repeated .tensorflow.metadata.v0.DatasetFeatureStatistics datasets = 1;</code>
   */
  DatasetFeatureStatistics getDatasets(int index);
  /**
   * <code>repeated .tensorflow.metadata.v0.DatasetFeatureStatistics datasets = 1;</code>
   */
  int getDatasetsCount();
  /**
   * <code>repeated .tensorflow.metadata.v0.DatasetFeatureStatistics datasets = 1;</code>
   */
  java.util.List<? extends DatasetFeatureStatisticsOrBuilder>
      getDatasetsOrBuilderList();
  /**
   * <code>repeated .tensorflow.metadata.v0.DatasetFeatureStatistics datasets = 1;</code>
   */
  DatasetFeatureStatisticsOrBuilder getDatasetsOrBuilder(
      int index);
}
