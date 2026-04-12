<h1><img width=80 src="https://github.com/MrQuackDuck/DiscordVerificator/assets/61251075/2a163eb9-515e-409a-b581-94a4fa513d91" /> <div>DiscordVerificator</div></h1>

<p>
  <a href="https://www.java.com/"><img src="https://img.shields.io/badge/Java-gray?color=C8273F" /></a>
  <a href="https://hub.spigotmc.org/javadocs/spigot/"><img src="https://img.shields.io/badge/Spigot_API-gray?color=F07427&logo=spigotmc&logoColor=FFFFFF" /></a>
  <a href="https://jda.wiki/"><img src="https://img.shields.io/badge/JDA-gray?color=5662F6&logo=discord&logoColor=FFFFFF" /></a>
  <a href="https://github.com/vshymanskyy/StandWithUkraine"><img src="https://raw.githubusercontent.com/vshymanskyy/StandWithUkraine/main/badges/StandWithUkraine.svg"></a>
</p>

 **DiscordVerificator** is a **Spigot** plugin that allows you to do player authentication using **Discord bot**.<br>

> [!WARNING]
> This plugin is **intended** to be used on **private servers** with the **manual player addition** because it involves you to manually link each player to their **Discord profile**.

 It was developed as an **alternative** for password-based authorization like `/login <password>` on servers with `online-mode` set to `false` (_in server.properties_).


 ## 🤔 How it works?

This plugin enables players to **link their usernames to their Discord profiles**. <br/>

The **linking process** is **controlled by the administrator** of the server. <br/>
In order **to link** the account, the **admin** should run `/link <Player> <Discord ID>`. ([how to get discord id?](https://youtu.be/RzTWH0g2xbo?si=oQT2rCSuf6B3Z5kY))
<img src="https://github.com/MrQuackDuck/DiscordVerificator/assets/61251075/50193702-ed0f-4b60-9884-58754a25328d">

Then, **when a player joins the server**, the **verification code appears**.<br>
To join the server, the player should run the seen command to the **Discord bot** you've configured: <br>
<img height=250 src="https://github.com/MrQuackDuck/DiscordVerificator/assets/61251075/1ad48c69-198b-48dc-8f5a-837312f094fa"><br>
<img src="https://github.com/MrQuackDuck/DiscordVerificator/assets/61251075/235dfef9-f390-4e2b-9bbe-d8e525425fe8"><br>
<img src="https://github.com/MrQuackDuck/DiscordVerificator/assets/61251075/c2758242-a3cd-4ef3-b6ec-83eb68e9438f">

> [!NOTE]
> **Verification** is required **once per IP change**

> [!CAUTION]
> The plugin **will prevent a player from joining** if it wasn't linked to **Discord** profile yet:
> <img height=200 src="https://github.com/MrQuackDuck/DiscordVerificator/assets/61251075/ef98c616-3c90-41cf-a111-ae49f416dc3c">

## 💻 Commands
- `/link <player> <discordId>` — links the player to its Discord profile. ([how to get discord id?](https://youtu.be/RzTWH0g2xbo?si=oQT2rCSuf6B3Z5kY))
- `/unlink <player>` — unlinks the player from its Discord profile.
- `/dvreload` — reloads the plugin (_including Discord bot_).
  
## 🔞 Permissions
- `discordVerificator.link` _(for **operators** by default)_ — Allows to use `/link <player> <discordId>`
- `discordVerificator.unlink` _(for **operators** by default)_ — Allows to use `/unlink <player>`
- `discordVerificator.reload` _(for **operators** by default)_ — Allows to use `/dvreload`

## 📄 Default config
> [!IMPORTANT]
> You should replace `DISCORD_BOT_TOKEN` with your **Discord bot token**.<br>
> **Otherwise, nothing will work!**

```yml
# 1. Create a Discord bot on the Discord Developer Portal: https://discord.com/developers/applications
# 2. Get the token from the "Bot" tab
# 3. Insert the token below
# 4. Run the "/dvreload" command or reload the server
# 5. Give your players access to send a command to the bot (e.g., invite it to your Discord server)
token: "DISCORD_BOT_TOKEN"

# Delay that a player must wait until getting a new verification code (in seconds)
codeDelay: 10

messages:
  "not-enough-permissions": "&cNot enough permissions!"
  "invalid-link-format": "&cInvalid format! Please use: /link <player> <discordId>"
  "invalid-unlink-format": "&cInvalid format! Please use: /unlink <player>"
  "invalid-user-id-format": "&cInvalid Discord ID format!"
  "successfully-linked": "&aSuccessfully linked!"
  "successfully-unlinked": "&aSuccessfully unlinked!"
  "player-already-linked": "&cThis player is already linked!"
  "player-was-not-linked": "&cThis player was never linked!"
  "account-not-linked": "&cYour account is not linked to a Discord profile yet."
  "bot-not-working": "&cThe Discord bot is not currently working!\nAsk the administrator to resolve this issue."
  "confirm-with-command": "&6Confirm your IP via our Discord bot\nUsage: &f&n/confirm %s"
  "wait-until-verification": "&cPlease wait until you can request a new code!\n&f&n%s seconds left."
  "error-occurred": "An error occurred!"
  "its-not-your-account": "The account you're trying to confirm is not linked to your Discord profile!"
  "allowed": "Allowed!"
  "allowed-to-join-from-ip": "Successfully allowed to join from `%s`!"
  "confirm-command": "Command to verify you on the Minecraft server"
  "verification-code-you-got": "Verification code you've received from the server"
  "invalid-code": "Invalid Code!"
  "invalid-code-description": "This verification code is not valid!"
  "invalid-usage": "Invalid usage!"
  "provide-code-please": "Please provide the verification code!"
  "user-not-found": "User not found!"
  "user-not-found-description": "It seems like your account hasn't been linked to any Minecraft username yet."
  "reloaded": "&#14C60D[DiscordVerificator] Reloaded!"
```

## ☂ Getting started

> [!IMPORTANT]
> Before getting started, make sure that the plugin's version is **compatible** with your server version.

1. Create a **new discord application** on <a href="https://discord.com/developers/applications/">Discord Developer Portal</a><br>
![image](https://github.com/MrQuackDuck/DiscordVerificator/assets/61251075/3322da7c-95b3-4ee0-a22a-c868c5f43aae)<br>
![image](https://github.com/MrQuackDuck/DiscordVerificator/assets/61251075/fa05b770-0ad0-42a6-833e-101ba06eee41)
1. Go to the **"Bot"** tab and click on the **"Reset token"** button<br>
![image](https://github.com/MrQuackDuck/DiscordVerificator/assets/61251075/f75a5bca-a28a-42b2-aa04-479842688280)<br>
![image](https://github.com/MrQuackDuck/DiscordVerificator/assets/61251075/be31fca3-2f50-4004-a9c9-fdf076bde60d)
1. Copy the token<br>
![image](https://github.com/MrQuackDuck/DiscordVerificator/assets/61251075/f2b15ed9-5999-4093-8c78-3ee27c490c28)
1. Download the plugin from <a href="https://github.com/MrQuackDuck/DiscordVerificator/releases">Releases</a> tab or from <a href="https://www.spigotmc.org/resources/discord-verificator.117794/">Spigot</a> page.
1. Put downloaded `.jar` into `./plugins` folder of your server.
1. Restart your server or enter `reload` command.
1. Go to `./plugins/DiscordVerificator` folder and open `config.yml`
1. Replace `DISCORD_BOT_TOKEN` with the token you've copied previously
1. Save the config and run `dvreload` command<br><br>
1. **Everything is done!** Now you can link players with the `link` command and<br> **invite** this bot **to your Discord server** (_to make them able to run `confirm <code>` command to the **Discord bot**_)
