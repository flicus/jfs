jfs
===

Jabber file syncronizer provides file sincronization service between peers possible for multiuser connection

Main terms:

jfs allow to sync contents beween participants called Peer. 
Each peer can be addressed by JID, so communucation goes over XMPP.
Each pair of peers syncing each own file set colled Repository. 
Every user can handle one or more repo's, one repo per peer.
Repositories consists of files stored locally, each file can belong to one or more repo. 
If file present in only one repo, it sincronized only with one peer. 
File belonging to more than one repo called multirepos. 
Multireposfile sincronized with as many peers as it belongs.



