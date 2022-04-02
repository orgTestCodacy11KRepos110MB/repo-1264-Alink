import unittest
from pyalink.alink import *
import numpy as np
import pandas as pd
class TestNaiveBayes(unittest.TestCase):
    def test_naivebayes(self):

        df_data = pd.DataFrame([
               [1.0, 1.0, 0.0, 1.0, 1],
               [1.0, 0.0, 1.0, 1.0, 1],
               [1.0, 0.0, 1.0, 1.0, 1],
               [0.0, 1.0, 1.0, 0.0, 0],
               [0.0, 1.0, 1.0, 0.0, 0],
               [0.0, 1.0, 1.0, 0.0, 0],
               [0.0, 1.0, 1.0, 0.0, 0],
               [1.0, 1.0, 1.0, 1.0, 1],
               [0.0, 1.0, 1.0, 0.0, 0]
        ])
        
        batchData = BatchOperator.fromDataframe(df_data, schemaStr='f0 double, f1 double, f2 double, f3 double, label int')
        
        colnames = ["f0","f1","f2", "f3"]
        ns = NaiveBayesTrainBatchOp().setFeatureCols(colnames).setLabelCol("label")
        model = batchData.link(ns)
        
        predictor = NaiveBayesPredictBatchOp().setPredictionCol("pred")
        predictor.linkFrom(model, batchData).print()
        colnames = ["f0","f1","f2", "f3"]
        # pipeline model
        ns = NaiveBayes().setFeatureCols(colnames).setLabelCol("label").setPredictionCol("pred")
        model = ns.fit(batchData)
        model.transform(batchData).print()
        pass