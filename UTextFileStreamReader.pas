unit UTextFileStreamReader;

{
https://github.com/stokito/TextFileReader
}

interface

uses
  Classes, SysUtils, Math;

procedure inputFileOpen(inputFilePath: string; var inputFile: TextFile); overload;
procedure inputFileReset(var inputFile: TextFile); overload;
procedure inputFileClose(var inputFile: TextFile); overload;
function inputFileNextString(var inputFile: TextFile; strLength: integer): string; overload;
function inputFileNextLine(var inputFile: TextFile): string; overload;
function inputFileNextChar(var inputFile: TextFile): char; overload;
function inputFileNextInt(var inputFile: TextFile): integer; overload;
function inputFileNextDouble(var inputFile: TextFile): double; overload;
function inputFileIsEof(var inputFile: TextFile): boolean; overload;
function inputFileIsEoln(var inputFile: TextFile): boolean; overload;
procedure inputFileSkipLn(var inputFile: TextFile); overload;
procedure inputFileSkipSpaces(var inputFile: TextFile; spacesCount: integer); overload;
function linesCount(inputFilePath: string): integer;

function inputFileOpen(inputFilePath: string): TFileStream; overload;
procedure inputFileReset(inputFileStream: TFileStream); overload;
procedure inputFileClose(inputFileStream: TFileStream); overload;
function inputFileNextString(inputFileStream: TFileStream; strLength: integer): string; overload;
function inputFileNextLine(inputFileStream: TFileStream): string; overload;
function inputFileNextChar(inputFileStream: TFileStream): char; overload;
function inputFileHasNextInt(inputFileStream: TFileStream): boolean;
function inputFileHasNextDouble(inputFileStream: TFileStream): boolean;
function inputFileNextInt(inputFileStream: TFileStream): integer; overload;
function inputFileNextDouble(inputFileStream: TFileStream): double; overload;
function inputFileIsEof(inputFileStream: TFileStream): boolean; overload;
function inputFileIsEoln(inputFileStream: TFileStream): boolean; overload;
procedure inputFileSkipLn(inputFileStream: TFileStream); overload;
procedure inputFileSkipSpaces(inputFileStream: TFileStream; spacesCount: integer); overload;
function inputFileHasLines(inputFileStream: TFileStream): boolean;

implementation

const
  CR: char = #13;
  LF: char = #10;

procedure inputFileOpen(inputFilePath: string; var inputFile: TextFile); overload;
begin
  AssignFile(inputFile, inputFilePath);
  reset(inputFile);
end;

procedure inputFileReset(var inputFile: TextFile);
begin
  Reset(inputFile);
end;

procedure inputFileClose(var inputFile: TextFile);
begin
  CloseFile(inputFile);
end;

function inputFileNextString(var inputFile: TextFile; strLength: integer): string;
var
  inputVariable: string;
  ch: char;
  i: integer;
begin
  inputVariable := '';
  i := 0;
  while (i < strLength) and not inputFileIsEof(inputFile) do
  begin
    ch := inputFileNextChar(inputFile);
    inputVariable := inputVariable + ch;
    Inc(i);
  end;
  Result := inputVariable;
end;

function inputFileNextLine(var inputFile: TextFile): string;
var
  inputVariable: string;
begin
  assert(not inputFileIsEof(inputFile));
  ReadLn(inputFile, inputVariable);
  Result := inputVariable;
end;

function inputFileNextChar(var inputFile: TextFile): char;
var
  inputVariable: char;
begin
  assert(not inputFileIsEof(inputFile));
  Read(inputFile, inputVariable);
  Result := inputVariable;
end;

function inputFileNextInt(var inputFile: TextFile): integer;
var
  inputVariable: integer;
begin
  assert(not inputFileIsEof(inputFile));
  Read(inputFile, inputVariable);
  Result := inputVariable;
end;

function inputFileNextDouble(var inputFile: TextFile): double;
var
  inputVariable: double;
begin
  assert(not inputFileIsEof(inputFile));
  Read(inputFile, inputVariable);
  Result := inputVariable;
end;

function inputFileIsEof(var inputFile: TextFile): boolean;
begin
  Result := eof(inputFile);
end;

function inputFileIsEoln(var inputFile: TextFile): boolean;
begin
  Result := eoln(inputFile);
end;

procedure inputFileSkipLn(var inputFile: TextFile);
begin
//  assert(not inputFileIsEof(inputFile)); // it's ok that there is not new line in the last line of the file
  ReadLn(inputFile);
end;

procedure inputFileSkipSpaces(var inputFile: TextFile; spacesCount: integer);
var
  ch: char;
  i: integer;
begin
  assert(not inputFileIsEof(inputFile));
  i := 0;
  while (i < spacesCount) and not inputFileIsEof(inputFile) do
  begin
    ch := inputFileNextChar(inputFile);
    assert(((ch = ' ') or (ch = #9)));
  end;
end;

// Migration to TFileStream;

procedure inputFileSeekBack(inputFileStream: TFileStream);
begin
  inputFileStream.Seek(inputFileStream.Position - 1, soBeginning);
end;

function inputFileReadChar(inputFileStream: TFileStream): char;
var
  buf: array[0 .. 0] of byte;
begin
  // for some reason Delphi doesn't provide ReadByte() method so we have to read a single byte buffer instead
  inputFileStream.Read(buf, 1);
  Result := char(buf[0]);
end;

function eatEoln(inputFileStream: TFileStream; ch: char): boolean;
var
  eolnWasEaten: boolean;
  chLf: char;
begin
  // if CR then break cycle, but before we should also read (possible) LF
  if (ch <> CR) and (ch <> LF) then
  begin
    eolnWasEaten := False;
    Result := eolnWasEaten;
    Exit;
  end;
  // last line can be without CR or CRLF so we can determine the string if EOF was reached
  if (ch = LF) or inputFileIsEof(inputFileStream) then
  begin
    eolnWasEaten := True;
    Result := eolnWasEaten;
    Exit;
  end;
  // at least one byte left which can be LF
  chLf := inputFileReadChar(inputFileStream);
  // if the byte wasn't LF then seek back
  if chLf <> LF then
    inputFileSeekBack(inputFileStream);
  eolnWasEaten := True;
  Result := eolnWasEaten;
end;

procedure eatSpaces(inputFileStream: TFileStream);
var
  ch: char;
begin
  while not inputFileIsEof(inputFileStream) do
  begin
    ch := inputFileReadChar(inputFileStream);
    if (ch <> ' ') and (ch <> #9) then
    begin
      inputFileSeekBack(inputFileStream);
      Exit;
    end;
  end;
end;

function linesCount(inputFilePath: string): integer;
var
  numberOfLines: integer;
  inputFileStream: TFileStream;
begin
  inputFileStream := nil;
  try
    inputFileStream := inputFileOpen(inputFilePath);
    numberOfLines := 0;
    while not inputFileIsEof(inputFileStream) do
    begin
      inputFileSkipLn(inputFileStream);
      Inc(numberOfLines);
    end;
    Result := numberOfLines;
  finally
    if inputFileStream <> nil then
      inputFileClose(inputFileStream);
  end;
end;

function inputFileOpen(inputFilePath: string): TFileStream;
var
  inputFileStream: TFileStream;
begin
  inputFileStream := TFileStream.Create(inputFilePath, fmOpenRead or fmShareDenyWrite);
  inputFileReset(inputFileStream);
  Result := inputFileStream;
end;

procedure inputFileReset(inputFileStream: TFileStream);
begin
  inputFileStream.Seek(0, soBeginning);
end;

procedure inputFileClose(inputFileStream: TFileStream);
begin
  inputFileStream.Free();
end;

function inputFileNextString(inputFileStream: TFileStream; strLength: integer): string;
var
  readedBytes, leftBytesInStream, bytesToRead: int64;
  inputVariable: string;
  ch: char;
begin
  leftBytesInStream := inputFileStream.Size - inputFileStream.Position - 1;
  bytesToRead := Min(leftBytesInStream, strLength);
  inputVariable := '';
  readedBytes := 0;
  while (readedBytes < bytesToRead) and not inputFileIsEoln(inputFileStream) do
  begin
    ch := inputFileReadChar(inputFileStream);
    inputVariable := inputVariable + ch;
    Inc(readedBytes);
  end;
  Result := inputVariable;
end;

function inputFileNextLine(inputFileStream: TFileStream): string;
var
  inputVariable: string;
  ch: char;
begin
  inputVariable := '';
  while not inputFileIsEof(inputFileStream) do
  begin
    ch := inputFileReadChar(inputFileStream);
    if eatEoln(inputFileStream, ch) then
      break;
    inputVariable := inputVariable + ch;
  end;
  Result := inputVariable;
end;

function inputFileIsEoln(inputFileStream: TFileStream): boolean;
var
  ch: char;
begin
  if inputFileIsEof(inputFileStream) then
  begin
    Result := True;
    Exit;
  end;
  ch := inputFileReadChar(inputFileStream);
  inputFileSeekBack(inputFileStream);
  Result := (ch = CR) or (ch = LF);
end;

function inputFileNextChar(inputFileStream: TFileStream): char;
var
  ch: char;
begin
  assert(not inputFileIsEof(inputFileStream));
  ch := inputFileReadChar(inputFileStream);
  Result := ch;
end;

function inputFileNextNumberStr(inputFileStream: TFileStream; floatComma: boolean): string;
var
  ch: char;
  inputValueStr: string;
  readedBytes: integer;
begin
  inputValueStr := '';
  for readedBytes := 1 to 16 do
  begin
    if inputFileIsEof(inputFileStream) then
      break;
    ch := inputFileReadChar(inputFileStream);
    if (readedBytes = 1) and ((ch = '-') or (ch = '+')) then
      inputValueStr := inputValueStr + ch
    else if (ch in ['0'..'9']) or (floatComma and (ch = '.')) then
      inputValueStr := inputValueStr + ch
    else
    begin
      inputFileSeekBack(inputFileStream);
      break;
    end;
  end;
  Result := inputValueStr;
end;

function inputFileHasNextNumber(inputFileStream: TFileStream; floatComma: boolean): boolean;
var
  inputValueStr: string;
  startPos: longint;
  hasNextValue: boolean;
begin
  startPos := inputFileStream.Position;
  eatSpaces(inputFileStream);
  inputValueStr := inputFileNextNumberStr(inputFileStream, floatComma);
  hasNextValue := inputValueStr = '';
  inputFileStream.seek(startPos, soBeginning);
  Result := hasNextValue;
end;

function inputFileHasNextInt(inputFileStream: TFileStream): boolean;
begin
  Result := inputFileHasNextNumber(inputFileStream, false);
end;

function inputFileHasNextDouble(inputFileStream: TFileStream): boolean;
begin
  Result := inputFileHasNextNumber(inputFileStream, true);
end;

function inputFileNextInt(inputFileStream: TFileStream): integer;
var
  inputValueStr: string;
  inputValue: integer;
  startPos, beginPos: longint;
begin
  startPos := inputFileStream.Position;
  eatSpaces(inputFileStream);
  assert(not inputFileIsEof(inputFileStream));
  beginPos := inputFileStream.Position;
  inputValueStr := inputFileNextNumberStr(inputFileStream, false);
  if inputValueStr = '' then
    raise Exception.Create('Unable to parse integer startPos: ' + IntToStr(startPos) + ' beginPos: ' + IntToStr(beginPos) + ' endPos: ' + IntToStr(inputFileStream.Position));
  inputValue := StrToInt(inputValueStr);
  Result := inputValue;
end;

function inputFileNextDouble(inputFileStream: TFileStream): double;
var
  inputValueStr: string;
  inputValue: double;
  startPos, beginPos: longint;
begin
  startPos := inputFileStream.Position;
  eatSpaces(inputFileStream);
  assert(not inputFileIsEof(inputFileStream));
  beginPos := inputFileStream.Position;
  inputValueStr := inputFileNextNumberStr(inputFileStream, true);
  if inputValueStr = '' then
    raise Exception.Create('Unable to parse double startPos: ' + IntToStr(startPos) + ' beginPos: ' + IntToStr(beginPos) + ' endPos: ' + IntToStr(inputFileStream.Position));
  inputValue := StrToFloat(inputValueStr);
  Result := inputValue;
end;

function inputFileIsEof(inputFileStream: TFileStream): boolean;
begin
  Result := inputFileStream.Size = (inputFileStream.Position + 1);
end;

procedure inputFileSkipLn(inputFileStream: TFileStream);
var
  ch: char;
begin
  while not inputFileIsEof(inputFileStream) do
  begin
    ch := inputFileReadChar(inputFileStream);
    if eatEoln(inputFileStream, ch) then
      Exit;
  end;
end;

function inputFileHasLines(inputFileStream: TFileStream): boolean;
var
  currentPos: int64;
  ch: char;
  fileHasSomeNonEmptyLines: boolean;
begin
  // check that there is some meaningful and non empty lines left in the file which should be parsed
  currentPos := inputFileStream.Position;
  while not inputFileIsEof(inputFileStream) do
  begin
    ch := inputFileReadChar(inputFileStream);
    if (ch <> CR) and (ch <> LF) and (ch <> ' ') and (ch <> #9) then
    begin
      inputFileStream.Seek(currentPos, soFromBeginning);
      fileHasSomeNonEmptyLines := True;
      Result := fileHasSomeNonEmptyLines;
      Exit;
    end;
  end;
  fileHasSomeNonEmptyLines := False;
  Result := fileHasSomeNonEmptyLines;
end;

procedure inputFileSkipSpaces(inputFileStream: TFileStream; spacesCount: integer);
var
  ch: char;
  i: integer;
begin
  for i := 1 to spacesCount do
  begin
    ch := inputFileNextChar(inputFileStream);
    assert(((ch = ' ') or (ch = #9)));
  end;
end;

end.
