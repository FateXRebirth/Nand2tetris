// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

(MAIN)
   @count
   M=0
   @KBD
   D=M
   @BLACK
   D;JNE
   @WHITE
   D;JEQ
   @MAIN
   0;JMP
(BLACK)
   @8192
   D=A
   @count
   D=D-M
   @MAIN
   D;JEQ
   @SCREEN
   D=A
   @count
   A=M+D
   M=-1
   @count
   M=M+1
   @BLACK
   0;JMP
(WHITE)
   @8192
   D=A
   @count
   D=D-M
   @MAIN
   D;JEQ
   @SCREEN
   D=A
   @count
   A=M+D
   M=0
   @count
   M=M+1
   @WHITE
   0;JMP
