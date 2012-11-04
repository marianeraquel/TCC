/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package quicksort;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Progressable;

import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class TemporaryOutputFormat extends
		SequenceFileOutputFormat<DoubleWritable, DoubleWritable> {

	public RecordWriter<DoubleWritable, DoubleWritable> getRecordWriter(
			FileSystem ignored, JobConf job, String name, Progressable progress) throws IOException {
		
		
		Path dir = getWorkOutputPath(job);
		FileSystem fs = dir.getFileSystem(job);
		FSDataOutputStream fileOut = fs.create(new Path(dir, name), progress);
		RecordWriter<DoubleWritable, Text> out = new RecordWriter<DoubleWritable, Text>() {
			
			@Override
			public void write(DoubleWritable arg0, Text arg1) throws IOException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void close(Reporter arg0) throws IOException {
				// TODO Auto-generated method stub
				
			}
		};
		// return new TeraRecordWriter(fileOut, job);
		return null;

	}
	static class TeraRecordWriter implements RecordWriter<DoubleWritable, Text>{

		@Override
		public void close(Reporter arg0) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void write(DoubleWritable arg0, Text arg1) throws IOException {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	//
	// static class TeraRecordWriter extends LineRecordWriter<Text, Text> {
	// private static final byte[] newLine = "\r\n".getBytes();
	//
	// public TeraRecordWriter(DataOutputStream out, JobConf conf) {
	// super(out);
	// }
	//
	// public synchronized void write(Text key, Text value) throws IOException {
	// out.write(key.getBytes(), 0, key.getLength());
	// out.write(value.getBytes(), 0, value.getLength());
	// out.write(newLine, 0, newLine.length);
	// }
	//
	// public void close() throws IOException {
	// if (finalSync) {
	// ((FSDataOutputStream) out).sync();
	// }
	// super.close(null);
	// }
	// }
	//
	// public RecordWriter<Text, Text> getRecordWriter(FileSystem ignored,
	// JobConf job, String name, Progressable progress) throws IOException {
	// Path dir = getWorkOutputPath(job);
	// FileSystem fs = dir.getFileSystem(job);
	// FSDataOutputStream fileOut = fs.create(new Path(dir, name), progress);
	// return new TeraRecordWriter(fileOut, job);
	// }
}
