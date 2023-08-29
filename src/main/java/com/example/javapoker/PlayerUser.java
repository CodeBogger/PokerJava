package com.example.javapoker;

import java.util.List;
import java.util.Scanner;

public class PlayerUser extends Player {
    private String playerName;
    private int chipCount;
    public PlayerUser(int chipCount, String playerName) {
        super(chipCount, playerName);
        PlayerUser.this.playerName = playerName;
        PlayerUser.this.chipCount = chipCount;
    }
    public static TurnLogic.CHOICE preFlopTurn(Scanner scan, Player player) {
        TurnLogic.CHOICE choice = null;

        if(player.blindType == BlindType.BIGBLIND) {

            System.out.print("Enter your choice ('CHECK', 'FOLD', 'RAISE'): ");
            String userInput = scan.nextLine().trim();

            choice = switch (userInput) {
                case "CHECK" -> TurnLogic.CHOICE.CHECK;
                case "FOLD" -> TurnLogic.CHOICE.FOLD;
                case "RAISE" -> TurnLogic.CHOICE.RAISE;
                default -> {
                    System.out.println("Invalid choice. Please enter a valid option.");
                    yield preFlopTurn(scan, player);
                }
            };

        } else {
            System.out.print("Enter your choice ('CALL', 'FOLD', 'RAISE'): ");
            String userInput = scan.nextLine().trim();

            choice = switch (userInput) {
                case "CALL" -> TurnLogic.CHOICE.CALL;
                case "FOLD" -> TurnLogic.CHOICE.FOLD;
                case "RAISE" -> TurnLogic.CHOICE.RAISE;
                default -> {
                    System.out.println("Invalid choice. Please enter a valid option.");
                    yield preFlopTurn(scan, player);
                }
            };
        }
        return choice;
    }
    public static TurnLogic.CHOICE playerTurn(Scanner scan) {
        TurnLogic.CHOICE choice = null;

        System.out.print("Enter your choice ('CHECK', 'FOLD', 'RAISE'): ");
        String userInput = scan.nextLine().trim();

        choice = switch (userInput) {
            case "CHECK" -> TurnLogic.CHOICE.CHECK;
            case "FOLD" -> TurnLogic.CHOICE.FOLD;
            case "RAISE" -> TurnLogic.CHOICE.RAISE;
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

        System.out.print("Enter your choice ('CALL', 'FOLD', 'RAISE'): ");
        String userInput = scan.nextLine();

        switch (userInput) {
            case "CALL" -> choice = TurnLogic.CHOICE.CALL;
            case "FOLD" -> choice = TurnLogic.CHOICE.FOLD;
            case "RAISE" -> choice = TurnLogic.CHOICE.RAISE;
            default -> {
                System.out.println("Invalid choice. Please enter a valid option.");
                choice = callFoldRaise(amountToCall, scan, player);
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
