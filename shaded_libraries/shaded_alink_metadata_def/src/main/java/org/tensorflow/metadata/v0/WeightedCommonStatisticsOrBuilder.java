// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: statistics.proto

package org.tensorflow.metadata.v0;

public interface WeightedCommonStatisticsOrBuilder extends
    // @@protoc_insertion_point(interface_extends:tensorflow.metadata.v0.WeightedCommonStatistics)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Weighted number of examples not missing.
   * </pre>
   *
   * <code>double num_non_missing = 1;</code>
   * @return The numNonMissing.
   */
  double getNumNonMissing();

  /**
   * <pre>
   * Weighted number of examples missing.
   * Note that if the weighted column is zero, this does not count
   * as missing.
   * </pre>
   *
   * <code>double num_missing = 2;</code>
   * @return The numMissing.
   */
  double getNumMissing();

  /**
   * <pre>
   * average number of values, weighted by the number of examples.
   * </pre>
   *
   * <code>double avg_num_values = 3;</code>
   * @return The avgNumValues.
   */
  double getAvgNumValues();

  /**
   * <pre>
   * tot_num_values = avg_num_values * num_non_missing.
   * This is calculated directly, so should have less numerical error.
   * </pre>
   *
   * <code>double tot_num_values = 4;</code>
   * @return The totNumValues.
   */
  double getTotNumValues();
}
