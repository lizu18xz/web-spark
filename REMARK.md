
### 打包,
```text
将依赖包单独提出
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <executions>
        <execution>
            <id>copy</id>
            <phase>package</phase>
            <goals>
                <goal>copy-dependencies</goal>
            </goals>
            <configuration>
                <!--指定的依赖路径-->
                <outputDirectory>
                    ${project.build.directory}/lib
                </outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>

添加maven-jar-plugin插件,使用maven-jar-plugin插件替换spring-boot-maven-plugin进行打包操作
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <configuration>
        <archive>
            <manifest>
                <!--addClasspath表示需要加入到类构建路径-->
                <addClasspath>true</addClasspath>
                <!--classpathPrefix指定生成的Manifest文件中Class-Path依赖lib前面都加上路径,构建出lib/xx.jar-->
                <classpathPrefix>lib/</classpathPrefix>
                <mainClass>com.xxxxx</mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>


```
### 准备
```text
spark-env.sh
HADOOP_CONF_DIR=/home/hadoop/app/hadoop-2.6.0-cdh5.7.0
```

### 依賴
```
因为打包完成后lib下面会包含所有的包,包括spark相关,但是会引起很多jar包冲突的问题,所以我们需要排除spark相关的jar包。当在
服务器运行的时候,我们安装的spark已经提供了那些包,因此我们
找到jar包下的MANIFEST.MF文件,里面有我们打包完成后启动web项目的依赖Class-Path: 
web-spark-1.0-SNAPSHOT.jar\META-INF\MANIFEST.MF


获取lib,不包含spark相关的
cp lib/spring-boot-starter-web-2.0.2.RELEASE.jar lib/spring-boot-starter-json-2.0.2.RELEASE.jar tmp
cp lib/jackson-datatype-jdk8-2.9.5.jar lib/jackson-datatype-jsr310-2.9.5.jar lib/jackson-module-parameter-names-2.9.5.jar lib/spring-boot-starter-tomcat-2.0.2.RELEASE.jar tmp
cp lib/tomcat-embed-core-8.5.31.jar lib/tomcat-embed-el-8.5.31.jar lib/tomcat-embed-websocket-8.5.31.jar lib/spring-web-5.0.6.RELEASE.jar tmp
cp lib/spring-beans-5.0.6.RELEASE.jar lib/spring-webmvc-5.0.6.RELEASE.jar lib/spring-aop-5.0.6.RELEASE.jar lib/spring-context-5.0.6.RELEASE.jar tmp
cp lib/spring-expression-5.0.6.RELEASE.jar lib/spring-boot-starter-2.0.2.RELEASE.jar lib/spring-boot-2.0.2.RELEASE.jar lib/spring-boot-starter-logging-2.0.2.RELEASE.jar tmp
cp lib/log4j-to-slf4j-2.10.0.jar lib/log4j-api-2.10.0.jar lib/javax.annotation-api-1.3.2.jar lib/spring-core-5.0.6.RELEASE.jar lib/spring-jcl-5.0.6.RELEASE.jar lib/snakeyaml-1.19.jar tmp
cp lib/spring-boot-autoconfigure-2.0.2.RELEASE.jar lib/slf4j-nop-1.7.2.jar tmp
cp lib/slf4j-api-1.7.2.jar lib/validation-api-1.1.0.Final.jar lib/hibernate-validator-5.2.4.Final.jar lib/jboss-logging-3.2.1.Final.jar tmp
cp lib/classmate-1.1.0.jar lib/scala-library-2.11.8.jar lib/paranamer-2.3.jar tmp
cp lib/jul-to-slf4j-1.7.16.jar lib/jackson-module-scala_2.11-2.9.5.jar tmp
cp lib/scala-reflect-2.11.11.jar lib/jackson-core-2.9.5.jar lib/jackson-annotations-2.9.5.jar lib/jackson-databind-2.9.5.jar lib/jackson-module-paranamer-2.9.5.jar tmp

mv tmp lib
```


### 提交到yarn
```text
提交:

spark-submit --master yarn \
--name web-spark \
--class com.demo.WebApplication \
--executor-memory 1G \
--num-executors 1 \
/home/hadoop/jars/web-spark-1.0-SNAPSHOT.jar

Error: Could not find or load main class org.apache.spark.deploy.yarn.ExecutorLauncher

1.因为在spark-default.conf增加了配置
spark.yarn.jars=hdfs://hadoop003:8020/sparkjars/*
所以需要把相关的包上传上去
cd /home/hadoop/app/spark-2.2.0-bin-2.6.0-cdh5.7.0/jars
hdfs dfs -put * hdfs://hadoop003:8020/sparkjars/

spark-submit --master yarn \
--name web-spark \
--jars $(echo /home/hadoop/app/spark-2.2.0-bin-2.6.0-cdh5.7.0/jars/*.jar | tr ' ' ',') \
--class com.demo.WebApplication \
--executor-memory 1G \
--num-executors 1 \
/home/hadoop/jars/web-spark-1.0-SNAPSHOT.jar


结果:

+-----+-----+
|col01|col02|
+-----+-----+
|    a|    g|
|    b|    d|
|    c|    f|
+-----+-----+


+-----+
|col01|
+-----+
|    a|
|    b|
|    c|
+-----+

http://192.168.76.142:8088/cluster/scheduler

```

```text
后续完善为具体的功能!TODO

```