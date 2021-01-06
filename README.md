# excel-demo

[修改POI源码，优化百万级大数据量Excel导出](https://blog.csdn.net/Shadow_Light/article/details/110387794)

运行项目前请先执行命令，将jar包导入本地仓库

    mvn install:install-file -Dfile=lib/poi-4.0.1.jar -DartifactId=poi -DgroupId=org.apache.poi -Dversion=4.0.1-SNAPSHOT -Dpackaging=jar -DpomFile=lib/poi.pom -Dsources=lib/poi-4.0.1-sources.jar
    mvn install:install-file -Dfile=lib/poi-ooxml-4.0.1.jar -DartifactId=poi-ooxml -DgroupId=org.apache.poi -Dversion=4.0.1-SNAPSHOT -Dpackaging=jar -DpomFile=lib/poi-ooxml.pom -Dsources=lib/poi-ooxml-4.0.1-sources.jar
    mvn install:install-file -Dfile=lib/poi-ooxml-schemas-4.0.1.jar -DartifactId=poi-ooxml-schemas -DgroupId=org.apache.poi -Dversion=4.0.1-SNAPSHOT -Dpackaging=jar -DpomFile=lib/poi-ooxml-schemas.pom
