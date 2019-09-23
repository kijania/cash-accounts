Simple RESTful API for money transfers between accounts.


git clone https://github.com:kijania/cash-accounts.git
cd CashAccounts
mvn package
java -jar target/cash-accounts-1.0-SNAPSHOT.jar


For setting the web server and REST endpoints I have chosen Akka-Http.
Since classic, but heavy framework as Spring is forbidden, I chose Akka-Http which is a suite of libraries
 providing tools for building HTTP integration layer rather than framework used for building application core.
Akka HTTP is build on top of Akka-Actor which is useful library for dealing with concurrency and parallelism
 what I tried to used in the current program.
Furthermore I based on Akka-Http because I was working with it in Scala and it helped me
 immersed into Java language more gently.


To keep Backend test simple I have not extended the RESTFUL API with GET single account, DELETE account,
 PUT account (modify balance in one account), identifying account by just created unique hash.
I also not focus on authentication or hiding details about not existent recipient accounts to prevent security leak.