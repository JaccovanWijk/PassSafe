# Report PassSafe
PassSafe is an app where people can store passwords and acces them from other devices. The passwords are not only protected by passwords and encryptions, but also personal questions that a user has to fill in.

<img src="https://github.com/JaccovanWijk/PassSafe/blob/master/doc/AccountsActivity.png" width="288" height="512" > 

## Functionality
The app consists of seven activities, one helper class and two model classes for objects.
### Activities
#### LoginActivity
The user is able to log in, surprisingly, using the Firebase authentication. The user can also be redirected to the *RegisterActivity*. If the login is succesful the user will be directed to the *AccountsActivity*. (This is implemented with the following functions:)
(##### loginClicked
This function listens for the button to be pressed so it can check the input. If the input checks out it checks with firebase if the username and password match. If that's the case then it will check if the e-mail is verified. If everything checks out the user will be directed to the *AccountsActivity* and the password will be stored in the background for later use in encrypting data. )
#### RegisterActivity
In this activity the user is able to register an account using the Firebase authentication. It checks if the input will be allowed by Firebase and makes an account if everything is fine. It also send the user an activationmail so he can verify his Firebase authentication account. Finaly it creates an activationkey for the user, which he uploads encrypted to the Firebase realtime database. The user will be directed to the *QuestionActivity* after a succesful registration. 
#### QuestionsActivity
This activity consists of two main functionalities:
* If the user is registering and has to provide an answer for a random question, then this activity will display a random question and display it. If the user answers it in a correct way, i.e. not empty, then the question and the encrypted answer will be added to an *Question* object, which is uploaded to the Firebase database. The user will return to the *LoginActivity* so he can acces his account.
* If the user is requesting an account, then this activity will load in the questions he has filled in from the Firebase database, and display one of them. If the user inputs a answer, the answer will be checked. If it's correct, then the user will be directed to the *PasswordActivity*, where the accountdetails are displayed.
#### AccountsActiviy
If its the first time a user opens this activity it wil mostly be empty. This is because the user has not added any accounts yet. Clicking the addbutton will direct the user to the *NewAccountActivity*. If the user has accounts displayed he/she is able to click them. The user will be directed to the *QuestionActivity*.
#### PasswordActivity
After answering the question correctly the accountdetails will be showed here. The activity also has two buttons that can copy the username or password respectifly. 
#### NewAccountActivity
The user will be uploading the accounts in this activity. It's possible to give the account an accounttype, an username and a password. The password can also be generated randomly. After pressing the acceptbutton the accounttype, encrypted username and enncrypted password will be added to a *Account* object, which is uploaded to the Firebase database. The user will be directed to the *AccountsActivity*, where the just uploaded account will be added. 
#### SettingsActivity
In almost all of the other activities ther is a optionmenu displayed in the top-right corner. This will give the options "Log Out" and "Settings". Clicking the latter one will direct the user to this activity, where you have two options:
* Changing the password of your account, which will delete all of your data unfortunately. More details about this later.
* Adding a question, where the user can choose one of the questions he/she has not answered yet. If they provide the right password, the question will be added to the Firebase database, just like the second functionality of the *QuestionActivity*. 
### Helper Class
#### FirebaseHelper
In all of the activities the link with Firebase authentication is locally managed. 
