# graphex
A regex engine that traverses a DFA to find all matching lines in a given file.  
NFA is created using Thompson Construction.  
DFA is built by performing subset construction on the NFA.  
Currently supports operations: * | ()  
Generating the NFA and DFA into DOT language is available as an option.  

# Usage
graphviz is required for rendering the NFA and DFA.  
Build: `javac graphex/*.java`  
Run: `java graphex.Grep [-n NFA-FILE] [-d DFA-FILE] REGEX FILE`  
Render the NFA or DFA into a png using `dot -Tpng NFA-FILE -o OUTPUT-FILE`  

Refer to render.sh and run.sh for an example.
