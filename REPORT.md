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
* If the user is registering and has to provide an answer for a random question, then this activity will display a random question and display it. If the user answers it in a correct way, i.e. not empty, then the question and the encrypted answer will be added to an *Question* object, which is uploaded to the Firebase realtime database. The user will return to the *LoginActivity* so he can acces his account.
* If the user is requesting an account, then this activity will load in the questions he has filled in from the Firebase realtime database, and display one of them. If the user inputs a answer, the answer will be checked. If it's correct, then the user will be directed to the *PasswordActivity*, where the accountdetails are displayed.
