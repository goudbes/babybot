# Babybot
### An IRC bot based on PircBotX

#### Supported commands

When given an op, it autovoices the existing channel users and everyone who joins the channel.

##### Current time
Command::

`?time`

Example: 

`user033 ?time`
`babyboten> user033: 2021/07/19 14:02:52`

##### Weather
Command:

`?weather <location>`

Example: 

`user033 ?weather Amsterdam`
`babyboten> user033: Vêrvarsel for: Amsterdam Koordinatar: 52°22'26"N 4°53'23"E Temperatur: 21.79 °C Trykk: 1022.0 hPa Luftfukt: 65.0% Detaljert: scattered clouds Skydekke: 40.0% Vind: 3.0 m/s frå nordaust Landskode: NL Soloppgang: Mon Jul 19 05:41:37 CEST 2021 Solnedgang: Mon Jul 19 21:51:28 CEST 2021`

#### Configuration file

Configuration should be included in a file `config.txt` with the following structure:

` Name <bot name> `
` Server <server>`
` Login <login> `
` Realname <real name> `  
` Autojoin <channel name>`  
` Version <version> ` 

#### Run: 

`java -cp .:commons-codec-1.10.jar:commons-lang3-3.4.jar:guava-19.0.jar:JMegaHal.jar:json-lib-2.4-jdk15.jar:owm-japis-2.5.0.3.jar:pircbotx-2.1.jar:slf4j-api-1.7.21.jar:slf4j-ext-1.7.21.jar:slf4j-simple-1.7.21.jar:BabyBot.jar babybot.MyListener`