set DESTINATION_FOLDER=windows\java-runtime

rmdir /Q /S "%DESTINATION_FOLDER%"
"%JAVA_HOME%/bin/jlink" --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.desktop,java.xml,java.naming,jdk.accessibility --output %DESTINATION_FOLDER%
