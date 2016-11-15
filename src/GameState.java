public enum GameState {
  WAITING,  // wait for players to connect before showing game panel
  NEW,      // players will start a new game without having to connect
  INGAME,   // game has begun
  END;      // game ended; no more words to guess
}
