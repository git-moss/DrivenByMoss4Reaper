set DESTINATION_FOLDER=windows\java-runtime

rmdir /Q /S "%DESTINATION_FOLDER%"
"%JAVA_HOME16%/bin/jlink" --no-header-files --no-man-pages --compress=1 --strip-debug --add-modules java.desktop,java.instrument,java.management,java.naming,java.sql,java.logging,jdk.accessibility --output %DESTINATION_FOLDER%
