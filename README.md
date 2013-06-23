basic softwarewolves Scala bot
==============================

An implementation of a bot to play the digital version of the werewolves party game.
The bot does not do much - it implements the lazy villager story.

More information on the softwarewolves game can be found at : [Softwarewolves documentation][1].

## Setting up the project


### 1. Get the code 

There are several possibilities:
* Download the project as a zipfile from github (github button somewhere on page). 
* Fork the project to your own github repository (github button somewhere on page), then clone it. This requires a github account.
* Clone the repository to your own computer. This requires git to be installed on your system. For cloning, you can use your favorite git tool or the following command:

    git clone https://github.com/JohanPeeters/basic-softwarewolves-Scala-bot.git
    
### 2. Install the project

Prerequisites are scala and sbt.
All other dependencies are pulled in by sbt.
If you do not know sbt, perhaps this bot is not for you.

### 3. Running tests

To run the tests, cd into the git repository and run

    $ sbt test
    
or start an interactive sbt session

    $ sbt
    
and let it detect when the code changes to trigger the tests:

    > ~ test
    
### 4. Configure and run the bot

The bot 
* connects as `francis@softwarewolves.org` to `softwarewolves.org`
* tells the game co-ordinator on that server that it wants to play
* joins the game room as 'Frank' when invited
* says 'howdy' when it joins the room.

Here are some things you may want to change and where to do it:
* in order to change the bot's credentials, change `name` and `passwd` in `Villager.scala`. Make sure that these are valid credentials on the target server.
* to change the target server, change `xmppSrv` in `Villager.scala`.
* to change the name by which the bot is known in the game, change `nickname` in `Villager.scala`.
    
In order to run this bot:

    $ sbt run
    

