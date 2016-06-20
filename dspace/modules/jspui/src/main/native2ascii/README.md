native2ascii
=============

This foler contains the native encoded translation message resource. You need to convert them to ascii code and put to the `resource` folder before `mvn package` to avoid encoding problems  
Here is how it's done with zh_TW as example:  

```
native2ascii Messages_zh_TW.properties ../resource/Messages_zh_TW.properties
```

Result: `中國` => `\u4e2d\u56fd`

### To turn back 

```
native2ascii -reverse ascii_encoded.txt output.txt
```

Result: `\u4e2d\u56fd` => `中國`

[More info](https://docs.oracle.com/javase/7/docs/technotes/tools/solaris/native2ascii.html)

