all:
	rm -rf output_dir temp_dir	
	javac -classpath hadoop-core-1.2.1.jar -d units *.java 
	jar -cvf units.jar -C units/ . 
	$$HADOOP_HOME/bin/hadoop jar units.jar hadoop.CommentRanker input_dir temp_dir output_dir
	cd bridge_utils && g++ -std=c++11 produceTopWordList.cpp -o produceTopWordList && ./produceTopWordList
	cd bridge_utils && g++ -std=c++11 produceIrrelevantCommentList.cpp -o produceIrrelevantCommentList && ./produceIrrelevantCommentList
	cd bridge_utils && gedit key-score.txt
	cd bridge_utils && gedit irrelevant_comment-score.txt
