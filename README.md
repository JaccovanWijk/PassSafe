# PassSafe
#### Jacco van Wijk
## Summary
In this app you can save your passwords for all your accounts. It's also able to generate random passwords for you.
## Problem
Forgetting a password is something that happens to everyone. This creates the idea of choosing a really easy password. This is cracked easily though. The harder the password the better. But reusing a really hard password for everything is also dangerous. If one of your accounts is compromised you’ll be very vulnerable to hackers. Therefore generating a random password for every new account would be the best way to go, if you can remember all of them.  

## Solution
If you can store your passwords in a secure app, which is also able to generate random passwords, you’ll never have the problem of losing a password again. Your passwords are harder to crack as well.

![alt text](https://github.com/JaccovanWijk/PassSafe/blob/master/doc/screens-proposal-app.png)

### Main Features
* Login
* Find password
* Generate a new password
* Adding acounts
* Deleting accounts (after asking if you really want to)
* Security with personal questions
* (Optional) Extra layer security with automatic reset of the password if questions are answered wrong


## Prerequisites
### Data Source
* I’m still looking for an API that’ll return a random personal question like “what was your first pets name”;
* (Optional) API with nice animations.
### External Components
I’ll be using Firebase to store all encrypted passwords.
### Hardest Part
The hardest part will probably be the encrypting. I’d like to experiment with this and make it as secure as possible with the available recourses.
#### Encryption
When logging in a key will be created which encrypts and decrypts your passwords. Therefore writing in the firebase goes as follows:
* Get key. This wil be the hashed password. Hash with [Base64](https://gist.github.com/EmilHernvall/953733);
* Encrypting the password with [AES encryption](https://aesencryption.net/);
* Write to firebase.

Reading from firebase goes as follows:
* Get key; This wil be the hashed password. Hash with [Base64](https://gist.github.com/EmilHernvall/953733);
* Read from firebase;
* Decrypting the password with [AES encryption](https://aesencryption.net/).
