# PassSafe

![](https://github.com/JaccovanWijk/PassSafe/blob/master/doc/lock_icon.png)

Jacco van Wijk  
Programmeerproject  
Minor Programmeren 2018  


## The App
PassSafe is an app that can store your passwords in an online database, so you can acces them from any device. This way you don't have to remember all of your passwords. All personal information is saved encrypted using a personal encryptionkey. You'll also recieve an activationkey, which is another step to make your account extra secure. It's possible to add accounts, overlook all of your accounts, see account details, and edit your profile. You can also generate a random password for your account. 

Login Activity            |  Register Activity       |   Question Activity    |   Accounts Activity  |
:------------------------:|:------------------------:|:----------------------:|:--------------------:|
![](https://github.com/JaccovanWijk/PassSafe/blob/master/doc/LoginActivity.png)  |  ![](https://github.com/JaccovanWijk/PassSafe/blob/master/doc/RegisterActivity.png)  |![](https://github.com/JaccovanWijk/PassSafe/blob/master/doc/QuestionActivity.png)  |![](https://github.com/JaccovanWijk/PassSafe/blob/master/doc/AccountsActivity.png)  |

Password Activity         |  New Account Activity    |   Settings Activity 1  |  Settings Activity 2 |
:------------------------:|:------------------------:|:----------------------:|:--------------------:|
![](https://github.com/JaccovanWijk/PassSafe/blob/master/doc/PasswordActivity.png)  |  ![](https://github.com/JaccovanWijk/PassSafe/blob/master/doc/NewAccountActivity.png)  |![](https://github.com/JaccovanWijk/PassSafe/blob/master/doc/SettingsActivity1.png)  |![](https://github.com/JaccovanWijk/PassSafe/blob/master/doc/SettingsActivity2.png)  |

## Sources
In this app accounts and storing data is managed with [Firebase](firebase.google.com). The authentication feature is used for logging in and the realtime database is used for storing the passwords, questions and the activationkeys. 

### Logo
The logo is made by [GraphicLoads](http://www.iconarchive.com/artist/graphicloads.html) and can be found at http://www.iconarchive.com/show/polygon-icons-by-graphicloads/lock-icon.html.

### Encryption
For the encryption the app uses a altered version of this [AES encryption](https://aesencryption.net/). Furthermore it uses Base64 to encode and decode.  

### Animation
For the animation between the different activities https://codinginflow.com/tutorials/android/slide-animation-between-activities is used.

## Copyright
https://github.com/JaccovanWijk/PassSafe/blob/master/LICENSE

## Demo
[Demo video(Dutch)](https://youtu.be/5Jpe5htDZ_o)
