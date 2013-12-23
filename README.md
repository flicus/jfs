jfs
===

Jabber file syncronizer provides file syncronization service between peers, possible for multiuser connection

Main terms:

jfs allows to sync content beween participants called peers. 
Each peer can be addressed by JID, so peers communicate over XMPP.
Each pair of peers syncing each own file set called [repository](doc/repository.md). 
Every user can handle one or more repo's, one repo per peer.
Repositories consists of files stored locally, each file can belong to one or more repo. 
If file present in only one repo, it [syncronized](doc/sync.md) only with one peer. 
File belonging to more than one repo called multirepos. 
Multirepos file syncronized with as many peers as it belongs.




