#define COMMAND_EXPLORE 0x01
#define COMMAND_SAVEAMOUNT 0x02

bool exploreEvent(byte cmd, byte *data, int dataLength) {
  switch (cmd) {
    case COMMAND_EXPLORE:
      if (dataLength < LAYER_LENGTH * 2) {
        return false;
      }
      for (int i = 0; i < LAYER_LENGTH; i++) {
        layerX[i] = min(data[i * 2] - 1, 100);
        layerY[i] = min(data[i * 2 + 1] - 1, 100);
      }
      writeLayer();
      return true;
    case COMMAND_SAVEAMOUNT:
      if (dataLength != LAYER_LENGTH) {
        return false;
      }
      for (int i = 0; i < LAYER_LENGTH; i++) {
        int d = max(min(data[i]-1, 100), 0);
        EEPROM[i] = d;
        amount[i] = d-50;
      }
      return true;
  }
}

