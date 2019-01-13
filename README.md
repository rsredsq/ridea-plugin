##EARLY WIP: RIdea - Remote Idea

**Caution: This is early wip prototype, bugs and missing features are expected**

A package that implements the Textmate's 'rmate' feature for Isntellij IDEA.

###Installation

- Install the plugin
- Install a rmate version

- Ruby version: https://github.com/textmate/rmate
- Bash version: https://github.com/aurora/rmate
- Perl version: https://github.com/davidolrik/rmate-perl
- Python version: https://github.com/sclukey/rmate-python
- Nim version: https://github.com/aurora/rmate-nim
- C version: https://github.com/hanklords/rmate.c
- Node.js version: https://github.com/jrnewell/jmate
- Golang version: https://github.com/mattn/gomate

###Usage

Server will be started automatically by default

Create an ssh tunnel

`ssh -R 52698:127.0.0.1:52698 user@example.org`

Go to the remote system and run

`rmate -p 52698 file`