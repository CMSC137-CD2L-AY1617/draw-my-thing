public enum PlayerState {
  READY,      // client is waiting for other clients before game starts
  DRAWING,    // draws the word for players to guess
  GUESSING;   // rest of the players
}
