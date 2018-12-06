all:
	rm -rf output_dir temp_dir	
	javac -classpath hadoop-core-1.2.1.jar -d units *.java 
	jar -cvf units.jar -C units/ . 
	$$HADOOP_HOME/bin/hadoop jar units.jar hadoop.CommentRanker input_dir temp_dir output_dir
	$$HADOOP_HOME/bin/hadoop fs -cat output_dir/* 

