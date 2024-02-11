# A tiny Truffle interpreter using Scala+Java

## Notes

- Truffle relies on Java annotations for all the specialization support, and communicating with Graal.  So, Scala isn't a viable choice.
- I had some trouble with accessing frame slot kinds.  FrameDescriptor has the kind info we pass in, but it's not automatically propagated to the VirtualFrame object.
    - One solution could be to initialize all variables to sentinel values of the relevant type.
    - The "Truffle way" of doing this seems like specialization on first encounter, or some type info propagation when constructing the AST so that variable nodes already have the right type.

## Relevant files

- `Lir.java` contains the language definition.  It has to be in Java because it needs to be annotated.
    - One pattern in that file is creation of a static instance that's passed to Truffle's Node constructors (see `node.scala`).
- `node.scala` contains the meat of the interpreter.
    - This includes the whole AST definition and the execute methods.
    - Functions are _root nodes_ of the AST.  So, they have the extra job of passing frame information to the `RootNode` constructor.
    - There is no support for passing in arguments to functions yet.
- `Main.scala` is a simple test program that builds the AST for a function, and runs it.

## Things to add

- Typed AST
  - Probably move to Java at this point
- Branching
- Argument support in functions
- Function calls
- Built-in (extern) functions
- More specializations
- Memory (pointers, I just made them point to another Value, but Truffle may require keeping track of some memory info)
- Hints to the compiler
  - When things get stable, branch probabilities, etc.

