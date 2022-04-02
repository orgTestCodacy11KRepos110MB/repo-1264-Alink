import unittest
from pyalink.alink import *
import numpy as np
import pandas as pd
class TestProphetTrainBatchOp(unittest.TestCase):
    def test_prophettrainbatchop(self):

        import time, datetime
        import numpy as np
        import pandas as pd
        
        downloader = AlinkGlobalConfiguration.getPluginDownloader()
        downloader.downloadPlugin('tf115_python_env_linux')
        
        data = pd.DataFrame([
        			[1,  datetime.datetime.fromtimestamp(1), 10.0],
        			[1,  datetime.datetime.fromtimestamp(2), 11.0],
        			[1,  datetime.datetime.fromtimestamp(3), 12.0],
        			[1,  datetime.datetime.fromtimestamp(4), 13.0],
        			[1,  datetime.datetime.fromtimestamp(5), 14.0],
        			[1,  datetime.datetime.fromtimestamp(6), 15.0],
        			[1,  datetime.datetime.fromtimestamp(7), 16.0],
        			[1,  datetime.datetime.fromtimestamp(8), 17.0],
        			[1,  datetime.datetime.fromtimestamp(9), 18.0],
        			[1,  datetime.datetime.fromtimestamp(10), 19.0]
        ])
        
        source = dataframeToOperator(data, schemaStr='id int, ts timestamp, val double', op_type='batch')
        
        prophetModel = source.link(\
                            ProphetTrainBatchOp()\
                                .setTimeCol("ts")\
                                .setValueCol("val")
                        )
        
        ProphetPredictBatchOp()\
            .setValueCol("data")\
            .setPredictNum(4)\
            .setPredictionCol("pred")\
            .linkFrom(
                 prophetModel,
                 source.link(
                    GroupByBatchOp()
        			    .setGroupByPredicate("id")
        			    .setSelectClause("id, mtable_agg(ts, val) as data")
                    )
            )\
            .print()
        pass