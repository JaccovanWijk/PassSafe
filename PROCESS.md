# Day 1 07 - 06 - 2018

After finishing the designdocument yesterday I started to realise what I had in mind. I created all activities and linked them up in the rigth way. Throughout the day I also looked into *Firebase* and how i wanted to implementit. The idea was improved on, but the implementation is far from done. Some other "highlights" of the day were:
* I realized that the ```FirebaseHelper``` I had in mind is kind of unnecessary, because every activity calls the firebase in a different way.
* I forgot to add a way to log out, so I implemented a optionmenu.
* I thought about the way I wanted to implement the firebase and made two javaclasses to support it.
* The firebase authentication is working but writing and reading still need a lot of work.

# Day 2 08 - 06 - 2018

Today I presented my idea and got some feedback, but all minor things. I also realised that using the ```FirebaseHelper``` will clean up my code and prefent dublicate code.

# Day 3 11 - 06 - 2018
* I started out trying to fix my firebase which been irritating me for the past few days.
* Then I fixed my ecryption and now it's ready for use!

# Day 4 12 - 06 - 2018
After some problems with nullpointers I figured out where the problem was (Uppercase/lowercase letters) and fixed it. Before I fixed it I looked at the way I wanted to close activities after intents. That way if you press the "Back" butten you won't end up somewhere you don't belong. Somewhere along the lines I uploaded a decendly working alpha version named alpha v1.0.

# Day 5 13 - 06 - 2018
Today I faced very little problems. I fixed the last nullpointers I could find and I made the login only possible with a verified email. I pushed a working version to git as my alpha v1.1.
Sometimes my dectypt function does not work. I can't figure out why and what's causing it. Forcing the bug to appear does not work so I hope it's a one time errer.
**UPDATE** Random passwords can't be decrypted and I can't figure out why?

# Day 6 14 - 06 - 2018
Today I made sure not only the passwords but also the username and the answers are encrypted on firebase. The decryption problem I faced yesterday was caused by a dublicate name. Took some time for me to find this one (Thanks Renske). After that i played with layout and coloring for the most part.
