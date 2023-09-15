package com.example.javapoker.PlayerObject;

import com.example.javapoker.GameLogic.SidePot;
import com.example.javapoker.GameLogic.TurnLogic;
import com.example.javapoker.Graphics.OutputSystem;
import com.example.javapoker.Graphics.UserInputHandler;

import java.util.Scanner;

import static com.example.javapoker.Graphics.HelloController.inputField;

public class PlayerUser extends Player {
    public PlayerUser(int chipCount, String playerName) {
        super(chipCount, playerName);
    }
    public static void checkFoldRaiseAllIn() {
        OutputSystem.print("Enter your choice ('CHECK', 'FOLD', 'RAISE', 'ALLIN'): ");}

    public static String userInput() {
        inputField.setDisable(false);
        String res = inputField.getText().trim();
        inputField.setDisable(true);
        return res;
    }
    public static TurnLogic.CHOICE preFlopTurn(Scanner scan, Player player) {
        TurnLogic.CHOICE choice;

        if(player.blindType == BlindType.BIGBLIND || player.blindType == BlindType.SMALLBLIND) {

            OutputSystem.print("Enter your choice ('CHECK', 'FOLD', 'RAISE', 'ALLIN'): ");
            String userInput = userInput();

            choice = switch (userInput) {
                case "CHECK" -> TurnLogic.CHOICE.CHECK;
                case "FOLD" -> TurnLogic.CHOICE.FOLD;
                case "RAISE" -> TurnLogic.CHOICE.RAISE;
                case "ALLIN" -> TurnLogic.CHOICE.ALLIN;
                default -> {
                    OutputSystem.print("Invalid choice. Please enter a valid option.");
                    yield preFlopTurn(scan, player);
                }
            };

        } else {
            OutputSystem.print("Enter your choice ('CALL', 'FOLD', 'RAISE', 'ALLIN'): ");
            String userInput = userInput();

            choice = switch (userInput) {
                case "CALL" -> TurnLogic.CHOICE.CALL;
                case "FOLD" -> TurnLogic.CHOICE.FOLD;
                case "RAISE" -> TurnLogic.CHOICE.RAISE;
                case "ALLIN" -> TurnLogic.CHOICE.ALLIN;
                default -> {
                    OutputSystem.print("Invalid choice. Please enter a valid option.");
                    yield preFlopTurn(scan, player);
                }
            };
        }
        return choice;
    }
    public static TurnLogic.CHOICE allInFold(Scanner scan, Player user) {
        TurnLogic.CHOICE choice;
        OutputSystem.print("Enter your choice ('FOLD', 'ALLIN'): ");
        String userInput = userInput();

        choice = switch (userInput) {
            case "FOLD" -> TurnLogic.CHOICE.FOLD;
            case "ALLIN" -> TurnLogic.CHOICE.ALLIN;
            default -> {
                OutputSystem.print("Invalid choice. Please enter a valid option.");
                yield playerTurn(scan);
            }
        };

        if(choice == TurnLogic.CHOICE.ALLIN) SidePot.addToSidePot(user);
        OutputSystem.print("Your choice: " + userInput + "\n\n");
        return choice;
    }
    public static TurnLogic.CHOICE playerTurn(Scanner scan) {
        TurnLogic.CHOICE choice;

        checkFoldRaiseAllIn();
        String userInput = userInput();

        choice = switch (userInput) {
            case "CHECK" -> TurnLogic.CHOICE.CHECK;
            case "FOLD" -> TurnLogic.CHOICE.FOLD;
            case "RAISE" -> TurnLogic.CHOICE.RAISE;
            case "ALLIN" -> TurnLogic.CHOICE.ALLIN;
            default -> {
                OutputSystem.print("Invalid choice. Please enter a valid option.");
                yield playerTurn(scan);
            }
        };

        OutputSystem.print("Your choice: " + userInput + "\n\n");
        return choice;
    }
    public static TurnLogic.CHOICE callFoldRaise(int amountToCall, Scanner scan, Player player) {
        TurnLogic.CHOICE choice;

        if(player.chips - amountToCall <= 0) {
            return allInFold(scan, player);
        } else {
            OutputSystem.print("Enter your choice ('CALL', 'FOLD', 'RAISE', 'ALLIN'): ");
            String userInput = userInput();

            switch (userInput) {
                case "CALL" -> choice = TurnLogic.CHOICE.CALL;
                case "FOLD" -> choice = TurnLogic.CHOICE.FOLD;
                case "RAISE" -> choice = TurnLogic.CHOICE.RAISE;
                case "ALLIN" -> choice = TurnLogic.CHOICE.ALLIN;
                default -> {
                    OutputSystem.print("Invalid choice. Please enter a valid option.");
                    choice = callFoldRaise(amountToCall, scan, player);
                }
            }
        }
        OutputSystem.print("\n\n" + player.getName() + " DECIDED TO " + choice);
        return choice;
    }
    public static int raiseTo(Scanner scan, Player player) {
        int raiseAmount;

        while (true) {
            try {
                OutputSystem.print("Enter the amount to raise: ");
                String userInput = userInput();
                raiseAmount = Integer.parseInt(userInput);
                if(raiseAmount > player.chips - 1) {
                    OutputSystem.print("Please input a value that you can afford.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                OutputSystem.print("Invalid input. Please enter a valid numeric value.");
            }
        }

        return raiseAmount;
    }
}