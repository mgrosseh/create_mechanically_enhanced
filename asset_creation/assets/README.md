Open create mod jar with an archive viewer (7zip, winrar, ark) and extract the contents of the `assets` directory 
into **this** assets directory.
Then repeat with minecraft 1.21.1 jar, copying its `assets` into this.
Also include Aeronautics-Bundled.jar similarly, here first extract the three mod jars inside the `jarjar` directory 
inside the bundled jar; then repeating as previously, copying the `assets` in the jars 

Make sure that `create-1.21.1-6.0.10.jar/assets/create` becomes `assets/create` (this assets) and not 
e.g. `assets/create-1.21.1-6.0.10/assets/create` (same with minecraft).
For aeronautics aka simulated project, there should be three directories inside assets, now beside `create` 
and `minecraft`, it should look as follows (... are many directories, they all have a `blockstates` subdirectory):
```
assets
|
- create
  |
  - blockstates
  ...
- minecraft
  ...
- aeronautics
  ...
- offroad
  ...
- simulated
  ...
- .gitignore
- README.md
```
