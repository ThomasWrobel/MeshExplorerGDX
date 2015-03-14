MeshExplorerGDX
===============

A crossplatform game made with LibGDX using SuperSimpleSemantics to allow anyone to contribute bits to it.

Its primarily made to test and demo the SSS system - an open transitional semantic database that can work with distributed data over many severs
simply be hosting list files. The SSS is a Java project, tested to run on GWT Javascript,Android and desktop. (but should run on anything else that can compile
basic java).
Because of the cross platform and distributed nature of the SSS system it made sense to make a game that can take advantage of the game
features.
As such "MeshExplore" is a game where you explore a secret network - a odd secondary layer to the internet with mysterious origins.
Anyone can add nodes to this network (IRL) simply by adding a text file or two to their domain, and getting another persons domain in the game to link to them.
(People wishing me to link their constributions up can contact me direct and I'll add their node connecting to one of the initial points in the game).

The gameplay will try to take advantage of basic transitional semantic knowledge for puzzles and interaction. Graphically it will be simple and overhead + cool shaders!

The game will also feature action in the form of a "concept gun", letting you blast mysterious infovour creatures eating the nearby data. The concept gun shots a concept (which is really a semantic nodes property) different creatures could respond to different concepts.
Much like cyberman are vulernable to gold. Bullets and lazers? bah. We will have cheese beams, metal beams, fish beams.....beams of anything!

I also welcome any help and contributions to the project!
If your interesting in help develop a distributed hacking/exploration game, let me know.

Currently working;

* Location nodes and links between them.
* GUI to let you pick up and use data items. 
* Nodes can be closed and need specific data to unlock
* Nodes have email like files explaining plot points and such.
* Nodes can be encrpyted with a language
* Nodes can contain ability files  
* - currently 3 abilitys implemented;
  - language ability that lets you read a location thats encypted
  - a "concept gun" ability that lets you shot things we a concept ray gun (the games main gimick)
  - inventory
* Concept gun can be used to shot creatures if they are vulerable to the concept (deduced by a query match that the creatures definition specifies)
* Hitblocking objects that creatures can hide behind.
* ...(probably more, I dont update this file too often...)


Partial implemented;
* creature populations that inhabit locations. They move and can be destroyed, but their look needs to be improved
* HTML/WebGL version. Being a LibGDX project it SHOULD work , but currently a shader issue is holding it back (I used fwidth() which apperently browsers dont like)
* Android version....who knows. In theory it should be easy to port. Ish. It compiles to Android but as yet it untested. I have decided it all with touch in mind at least.
* varying the look of the concept beam based on the shader
* more varience to the background as you move about





