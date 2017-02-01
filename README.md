# BlackJack
Java/Android Implementation for the famous game BlackJack

Please check the message:
====================
Java demo version updated on 2017-1-27
Sorry for uploading my work so late, because I was quit busy lately since
Chinese new year is coming and also loads of work to do in my company.

This is just a simple Java implementation, and Android implementation should
be based on it, please take some review.

Hopefully I'll upload Android UI version in a few days later before deadline(2017-2-2).
====================
Java full version updated on 2017-1-31
change list:
1. Treat dealer and player as one class, Add group to manage multiple players,
so that player and dealer can be switched smoothly and we can take turns
to be the dealer.
2. Add very simple AI support for AI players by take the default behavior.
3. Use state machine to divide logic modules.
4. We can interact with this game by using terminal input/output. We can add up to
5 players in a game, and take turns to be the dealer, which make this game
more funny.
6. optimize the architecture of the game world. Most importantly, current structure is
easy to be ported into a Socket multi-player version.

Since the main structure has been figured out, the Android version may be published
out tomorrow, since I'm running short of time the UI may be ugly.
====================
Android project updated on 2017-2-2