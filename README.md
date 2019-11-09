# TextFile reader

Migrate from Pascal's TextFile to Streams. The main purpose of is to simplify migration from Delphi to C# and Java. 
Also the parser can be used as is in Java and C#.

## Parsing text files in Pascal and Delphi

In Pascal and Delphi it is very widely used the [TextFile](https://wiki.freepascal.org/File_Handling_In_Pascal) to read human readable text files.

For example you have a simple text file with rows and columns with numbers:

       1.000 0.100
       2.000 0.200

And here is some Delphi code to count a sum of all the numbers:

```pascal
function calcSumInTextFile(inputFilePath: string): double;
var
  inputFile: TextFile;
  val1, val2, sum: double;
begin
  AssignFile(inputFile, inputFilePath);
  reset(inputFile);
  sum := 0;
  while not eof(inputFile) do
  begin
    ReadLn(inputFile, val1, val2);
    sum := sum + val1 + val2;
  end;
  CloseFile(inputFile);
  Result := sum;
end;
```

Here the main read and parsing is in the `ReadLn(inputFile, val1, val2)` call where we do three things:

 * Skip spaces `   `, parse a float number `1.000`, and write into `val1`
 * Skip space ` `, parse a float number `0.100`, and write into `val2`
 * Skip to the next line i.e. read `CRLF` or `LF`

The problem here is that the code uses `out` variables which are not supported by Java.
So we need to migrate the parsing to the linear form:

```pascal
function calcSumInTextFile(inputFilePath: string): double;
var
  inputFile: TextFile;
  val1, val2, sum: double;
begin
  inputFileOpen(inputFilePath, inputFile);
  sum := 0;
  while not inputFileIsEof(inputFile) do
  begin
    val1 := inputFileNextDouble(inputFile);
    val2 := inputFileNextDouble(inputFile);
    inputFileSkipLn(inputFile);
    sum := sum + val1 + val2;
  end;
  inputFileClose(inputFile);
  Result := sum;
end;
```

Another problem is that in Pascal the `TextFile` is the used for both writing and reading while in Java this operations are split into `InputStream` and `OutputStream`.
So we need to migrate to parsing of `TFileStream`.

To make the transition easier you can use [UTextFileStreamReader](./UTextFileStreamReader.pas) that has such functions like: 
 * `inputFileNextInt(var inputFile: TextFile): integer;`
 * `inputFileNextChar(var inputFile: TextFile): char;`
 * `inputFileNextString(var inputFile: TextFile; strLength: integer): string;`
 * `inputFileNextLine(var inputFile: TextFile; strLength: integer): string;`

On the first step of migration you need to use these functions instead of `AssignFile()`, `Read()`, `eof()`, `CloseFile()`;
On the second step you can easily change the `TextFile` variables to a `TFileStream` and all will work in the same manner:

```pascal
function calcSumInTextFile(inputFilePath: string): double;
var
  inputFile: TFileStream; // look ma, here is a Stream!
  val1, val2, sum: double;
begin
  inputFile := inputFileOpen(inputFilePath);
  sum := 0;
  while not inputFileIsEof(inputFile) do
  begin
    val1 := inputFileNextDouble(inputFile);
    val2 := inputFileNextDouble(inputFile);
    inputFileSkipLn(inputFile);
    sum := sum + val1 + val2;
  end;
  inputFileClose(inputFile);
  Result := sum;
end;
```

After conversion of your code from Delphi to C# or Java you can use the class `TextFileStreamReader` which have the same interface:

* For C# CSharp .NET 
* For Java [TextFileStreamReader.java](src/main/java/com/github/stokito/textfile/TextFileStreamReader.java)

## Additional methods

### Replace while not eof() with while inputFileHasLines()

Consider you have the same text file but with some empty lines in the end:

```
   1.000 0.100
   2.000 0.200
    

```

Or the same but with shown spaces and `CR` and `LF` line ending:

```
···1.000·0.100␍␊
···2.000·0.200␍␊
···␍␊
␍␊
```

Then on the line 3, which is empty or to be more precise have only three spaces, 
the program will try to read and parse it as float and it will fail with `EInOutError: Invalid inputs`.

And here it comes the a nice function from the library `inputFileHasLines()`:

```pascal
  while inputFileHasLines(inputFile) do
  begin
    ReadLn(inputFile, val1, val2);
    sum := sum + val1 + val2;
  end;
```

It will check for EOF or that there is no any lines without text