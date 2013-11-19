Repository
===

Repository is a set of files to exchange with one peer.
Files is not organized into any kind of folders, so repository is only set of files.
Physically each file can be places in different locations of local storage.
Because of flat structure of repository there are only 3 local operations:

- add file
- delete file
- change file

Each local change to be approved by both repository peer.
Peer made local change automatically approve this change.
Refuse remote change leads to different results depending of operation:

Cases started from local side
====
- Local add - remote add: 
Locally stored file registered in repository as successfully added.
Assume remote copy existance

- Local add - remote refuses:
Locally stored file removed from repository file set. 
Assume remote copy not created

- Local delete - remote delete:
Local file removed from repository file set and no more tracked. 
Ignore remote copy existance

- Local delete - remote refuses:
Local file removed from repository file set and no more tracked. 
Ignore remote copy existance

- Local change - remote change:
Fix change as revison. Local storage handle previous revision until each repository accepts this revision. 
Previous version storage policy configured by peer individually. 
Assume remote peer have at least current revision of file.

- Local change - remote refuses:
Local revision not registered in repository file set 
(but can be used by another repository)
Assume remote peer have only previous revison copy.

Cases started from remote side
====
- Remote add - local add:
Remote has new file, download & report as added. File added to repository fileset& 
Assume remote copy existance until peer delete or change file

- Remote add - local refuse:
Remote supply new file. Local can refuse adding accordinga any internal rules.
Forget. Nothing happened. 

- Remote delete - local delete:
Remote can ask for file removal (due misadding or any other doubts).
Local can accepts removal. Local is not tracked in this repository.
Phisical removal from storage is not manadatory.
Remote side assumes local does not have this file anymore.

- Remote delete - local refuses:
Remote can ask for file removal (due misadding or any other doubts).
Local can refuse removal. Local is not tracked in this repository.
Phisical removal from storage is not manadatory. 

- Remote change - local change:
Remote ask for file update with new data. 
Local can review changes and accepts it.
Local storage handle previous revision if file tracked in other repositories.
Local storage suggest new revison of file to other repositories.
Previous revision handled until each repository accepts change. 
Previous version storage policy after revision not used anywhere configured by peer individually. 
Assume remote peer now remembers local has this revision.

- Remote change - local refuses:
Local can refuse change of file. Revision is not fixed in repository 
and not translated to another repository's
Assume stop changing file.


