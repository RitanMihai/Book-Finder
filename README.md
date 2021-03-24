# Book-Finder
Book Finder application is a client-server application (gRPC) for educational purposes.

# Instalation

This projects (Client/Server) are Maven projects, so the installation should work smooth as butter.
- Set JAVA_HOME in o.s variables., mine is _C:\Program Files\Java\jdk-12.0.2_
- I did not test others SDKs beside [openjdk-15.0.2](https://jdk.java.net/15/). This can be download through IntelliJ IDE _(File -> Project Structure -> Project -> Project SDK )_
- You clone the repository and build each project separately. 
- Now use *Maven clean* and *Maven install*. Now we should have new files generated from our .proto file. The projects are ready to run.
- On server project we can run with Run button (Shift + f10), but on Client we must use the maven plugin javafx:run.


# Tools 
- [BloomRPC](https://github.com/uw-labs/bloomrpc) - A nice and simple GUI Client. If you want to implement just the server and test it, this might be super useful for you.
