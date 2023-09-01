package com.example.javapoker;

import java.util.Scanner;

public class PlayerUser extends Player {
    private String playerName;
    private int chipCount;
    public PlayerUser(int chipCount, String playerName) {
        super(chipCount, playerName);
        PlayerUser.this.playerName = playerName;
        PlayerUser.this.chipCount = chipCount;
    }
    public static void checkFoldRaiseAllIn() {System.out.print("Enter your choice ('CHECK', 'FOLD', 'RAISE', 'ALLIN'): ");}
    public static TurnLogic.CHOICE preFlopTurn(Scanner scan, Player player) {
        TurnLogic.CHOICE choice = null;

        if(player.blindType == BlindType.BIGBLIND) {

            checkFoldRaiseAllIn();
            String userInput = scan.nextLine().trim();

            choice = switch (userInput) {
                case "CHECK" -> TurnLogic.CHOICE.CHECK;
                case "FOLD" -> TurnLogic.CHOICE.FOLD;
                case "RAISE" -> TurnLogic.CHOICE.RAISE;
                case "ALLIN" -> TurnLogic.CHOICE.ALLIN;
                default -> {
                    System.out.println("Invalid choice. Please enter a valid option.");
                    yield preFlopTurn(scan, player);
                }
            };

        } else {
            System.out.print("Enter your choice ('CALL', 'FOLD', 'RAISE', 'ALLIN'): ");
            String userInput = scan.nextLine().trim();

            choice = switch (userInput) {
                case "CALL" -> TurnLogic.CHOICE.CALL;
                case "FOLD" -> TurnLogic.CHOICE.FOLD;
                case "RAISE" -> TurnLogic.CHOICE.RAISE;
                case "ALLIN" -> TurnLogic.CHOICE.ALLIN;
                default -> {
                    System.out.println("Invalid choice. Please enter a valid option.");
                    yield preFlopTurn(scan, player);
                }
            };
        }
        return choice;
    }
    public static TurnLogic.CHOICE allInFold(Scanner scan, int allInAmount, Player user) {
        TurnLogic.CHOICE choice = null;

        if(user.chips > allInAmount) {
            System.out.println("PLAYER CHIPS: "+user.chips);
            System.out.print("Enter your choice ('FOLD', 'ALLIN', 'CALL'): ");
            String userInput = scan.nextLine().trim();

            choice = switch (userInput) {
                case "FOLD" -> TurnLogic.CHOICE.FOLD;
                case "ALLIN" -> TurnLogic.CHOICE.ALLIN;
                case "CALL" -> TurnLogic.CHOICE.CALL;
                default -> {
                    System.out.println("Invalid choice. Please enter a valid option.");
                    yield playerTurn(scan);
                }
            };
            System.out.println("Your choice: " + userInput + "\n\n");
            return choice;
        }
        System.out.print("Enter your choice ('FOLD', 'ALLIN'): ");
        String userInput = scan.nextLine().trim();

        choice = switch (userInput) {
            case "FOLD" -> TurnLogic.CHOICE.FOLD;
            case "ALLIN" -> TurnLogic.CHOICE.ALLIN;
            default -> {
                System.out.println("Invalid choice. Please enter a valid option.");
                yield playerTurn(scan);
            }
        };

        System.out.println("Your choice: " + userInput + "\n\n");

        return choice;
    }
    public static TurnLogic.CHOICE playerTurn(Scanner scan) {
        TurnLogic.CHOICE choice = null;

        checkFoldRaiseAllIn();
        String userInput = scan.nextLine().trim();

        choice = switch (userInput) {
            case "CHECK" -> TurnLogic.CHOICE.CHECK;
            case "FOLD" -> TurnLogic.CHOICE.FOLD;
            case "RAISE" -> TurnLogic.CHOICE.RAISE;
            case "ALLIN" -> TurnLogic.CHOICE.ALLIN;
            default -> {
                System.out.println("Invalid choice. Please enter a valid option.");
                yield playerTurn(scan);
            }
        };

        System.out.println("Your choice: " + userInput + "\n\n");
        return choice;
    }
    public static TurnLogic.CHOICE callFoldRaise(int amountToCall, Scanner scan, Player player) {
        TurnLogic.CHOICE choice = null;

        if(player.chips - amountToCall <= 0) {
            return allInFold(scan, amountToCall, player);
        } else {
            System.out.println("Enter your choice ('CALL', 'FOLD', 'RAISE', 'ALLIN'): ");
            String userInput = scan.nextLine().trim();

            switch (userInput) {
                case "CALL" -> choice = TurnLogic.CHOICE.CALL;
                case "FOLD" -> choice = TurnLogic.CHOICE.FOLD;
                case "RAISE" -> choice = TurnLogic.CHOICE.RAISE;
                case "ALLIN" -> choice = TurnLogic.CHOICE.ALLIN;
                default -> {
                    System.out.println("Invalid choice. Please enter a valid option.");
                    choice = callFoldRaise(amountToCall, scan, player);
                }
            }
        }
        System.out.println("\n\n" + player.getName() + " DECIDED TO " + choice);
        return choice;
    }
    public static int raiseTo(Scanner scan) {
        int raiseAmount = 0;

        while (true) {
            try {
                System.out.print("Enter the amount to raise: ");
                String userInput = scan.nextLine();
                raiseAmount = Integer.parseInt(userInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric value.");
            }
        }

        return raiseAmount;
    }
}
