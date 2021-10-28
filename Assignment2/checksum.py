def readHex(file_name):
  # Read Hex from txt
  bin_arr = []
  with open(file_name, 'r') as file:
    for line in file.readlines():
      stream_arr = [line[i:i+4].zfill(4) for i in range(0, len(line), 4)]
      for word in stream_arr:
        bin_arr.append(bin(int(word, 16))[2:].zfill(16))
  return bin_arr
  
def addBin(arr):
  int_sum=0
  for word in arr:
    int_sum += int(word, 2)
  bin_sum = bin(int_sum)[2:]
  bin_sum = bin(int(bin_sum[0:-16], 2)+int(bin_sum[-16:], 2))[2:] if len(bin_sum) > 16 else bin_sum.zfill(16)
  return bin_sum

# Inverts the binary string (does not include 0b in beginning)
def getOnesComp(b):
  res = ''
  for i in range(len(b)-1, -1, -1):
    res = '1'+res if b[i]=='0' else '0'+res
  return res

# Reads from a file and adds the check sum to the file
def addCheckSum(file_in, file_out):
  bin_arr = readHex(file_in)
  
  bin_sum = addBin(bin_arr)

  checksum = getOnesComp(bin_sum)

  with open(file_out, "w") as file:
    for word in bin_arr:
      file.write(hex(int(word,2))[2:].zfill(4))
    file.write(hex(int(checksum,2))[2:].zfill(4))

def verifyCheckSum(file_out):
  bin_arr = readHex(file_out)

  bin_sum = addBin(bin_arr)
  
  if '0' in bin_sum:
    print("ERROR, SUM should be all 1's")

# If nothing is printed, means success
addCheckSum('f70_in.txt', 'f70_out.txt')
verifyCheckSum('f70_out.txt')
