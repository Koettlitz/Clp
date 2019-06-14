package de.dk.opt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import de.dk.opt.ex.ArgumentParseException;
import de.dk.opt.ex.InvalidOptionFormatException;
import de.dk.opt.ex.MissingArgumentException;
import de.dk.opt.ex.UnknownArgumentException;

/**
 * @author David Koettlitz
 * <br>Erstellt am 07.08.2017
 */
public class ArgumentParserTest {
   private static final String ARG_NAME0 = "Intensity";
   private static final String ARG_VALUE0 = "128";
   private static final String ARG_NAME1 = "Muttermilch";
   private static final String ARG_VALUE1 = "Butterknilch";

   private static final char OPT_KEY0 = 't';
   private static final String OPT_VALUE0 = "optvalue";
   private static final char OPT_KEY1 = 'v';
   private static final String OPT_LONG_KEY1 = "verbose";
   private static final String OPT_VALUE1 = "4";
   private static final char OPT_KEY2 = 'f';
   private static final String OPT_LONG_KEY2 = "format";
   private static final String OPT_VALUE2 = "#d.#m.YYYY";

   private static final String CMD_NAME0 = "run";
   private static final String CMD_NAME1 = "get";

   public ArgumentParserTest() {

   }

   private static ArgumentModel parse(ArgumentParser parser, String... args) {
      try {
         return parser.parseArguments(args);
      } catch (ArgumentParseException |
               IllegalArgumentException e) {
         fail(e.getMessage());
      }
      return null;
   }

   @Test
   public void singlePlainArgIsPresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .addArgument(ARG_NAME0)
                                                   .buildAndGet();
      ArgumentModel result = parse(parser, ARG_VALUE0);

      assertEquals(ARG_VALUE0, result.iterator().next());
      assertEquals(ARG_VALUE0, result.getArgumentValue(ARG_NAME0));
   }

   @Test
   public void multiplePlainArgumentsArePresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .addArgument(ARG_NAME0)
                                                   .addArgument(ARG_NAME1)
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, ARG_VALUE0, ARG_VALUE1);

      Iterator<String> iterator = result.iterator();
      assertEquals(ARG_VALUE0, iterator.next());
      assertEquals(ARG_VALUE0, result.getArgumentValue(ARG_NAME0));
      assertEquals(ARG_VALUE1, iterator.next());
      assertEquals(ARG_VALUE1, result.getArgumentValue(ARG_NAME1));
   }

   @Test
   public void singleOptionIsPresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .addOption(OPT_KEY0)
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, "-" + OPT_KEY0);
      assertTrue(result.isOptionPresent(OPT_KEY0), "Provided option not present.");
   }

   @Test
   public void multipleOptionsArePresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .addOption(OPT_KEY0)
                                                   .addOption(OPT_KEY1)
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, "-" + OPT_KEY0, "-" + OPT_KEY1);
      assertTrue(result.isOptionPresent(OPT_KEY0), "First provided option not present.");
      assertTrue(result.isOptionPresent(OPT_KEY1), "Second provided option not present.");
   }

   @Test
   public void singleOptionValueIsPresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .buildOption(OPT_KEY0)
                                                      .setExpectsValue(true)
                                                      .build()
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, "-" + OPT_KEY0, OPT_VALUE0);
      assertTrue(result.isOptionPresent(OPT_KEY0), "Provided option not present.");
      assertEquals(OPT_VALUE0, result.getOptionValue(OPT_KEY0));
   }

   @Test
   public void multipleOptionValuesArePresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .buildOption(OPT_KEY0)
                                                      .setExpectsValue(true)
                                                      .build()
                                                   .buildOption(OPT_KEY1)
                                                      .setExpectsValue(true)
                                                      .build()
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, "-" + OPT_KEY0, OPT_VALUE0, "-" + OPT_KEY1, OPT_VALUE1);
      assertTrue(result.isOptionPresent(OPT_KEY0), "First provided option not present.");
      assertEquals(OPT_VALUE0, result.getOptionValue(OPT_KEY0));
      assertTrue(result.isOptionPresent(OPT_KEY1), "Second provided option not present.");
      assertEquals(OPT_VALUE1, result.getOptionValue(OPT_KEY1));
   }

   @Test
   public void singleLongOptionIsPresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .buildOption(OPT_KEY1)
                                                      .setLongKey(OPT_LONG_KEY1)
                                                      .build()
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, "--" + OPT_LONG_KEY1);
      assertTrue(result.isOptionPresent(OPT_KEY1), "Provided long option not present.");
   }

   @Test
   public void singleLongOptionValueIsPresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .buildOption(OPT_KEY1)
                                                      .setLongKey(OPT_LONG_KEY1)
                                                      .setExpectsValue(true)
                                                      .build()
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, "--" + OPT_LONG_KEY1 + '=' + OPT_VALUE1);
      assertTrue(result.isOptionPresent(OPT_KEY1), "Provided long option not present.");
      assertEquals(OPT_VALUE1, result.getOptionValue(OPT_LONG_KEY1));
   }

   @Test
   public void multipleLongOptionsArePresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .buildOption(OPT_KEY1)
                                                      .setLongKey(OPT_LONG_KEY1)
                                                      .build()
                                                   .buildOption(OPT_KEY2)
                                                      .setLongKey(OPT_LONG_KEY2)
                                                      .build()
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, "--" + OPT_LONG_KEY1, "--" + OPT_LONG_KEY2);
      assertTrue(result.isOptionPresent(OPT_KEY1), "First provided long option not present.");
      assertTrue(result.isOptionPresent(OPT_KEY2), "Second provided long option not present.");
   }

   @Test
   public void multipleLongOptionValuesArePresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .buildOption(OPT_KEY1)
                                                      .setLongKey(OPT_LONG_KEY1)
                                                      .setExpectsValue(true)
                                                      .build()
                                                   .buildOption(OPT_KEY2)
                                                      .setLongKey(OPT_LONG_KEY2)
                                                      .setExpectsValue(true)
                                                      .build()
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, "--" + OPT_LONG_KEY1 + '=' + OPT_VALUE1, "--" + OPT_LONG_KEY2 + '=' + OPT_VALUE2);
      assertTrue(result.isOptionPresent(OPT_KEY1), "First provided long option not present.");
      assertEquals(OPT_VALUE1, result.getOptionValue(OPT_LONG_KEY1));
      assertTrue(result.isOptionPresent(OPT_KEY2), "Second provided long option not present.");
      assertEquals(OPT_VALUE2, result.getOptionValue(OPT_LONG_KEY2));
   }

   @Test
   public void multipleOptionsInOneTokenArePresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .addOption(OPT_KEY0)
                                                   .addOption(OPT_KEY1)
                                                   .addOption(OPT_KEY2)
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, "-" + OPT_KEY0 + OPT_KEY1 + OPT_KEY2);
      assertTrue(result.isOptionPresent(OPT_KEY0), "First option not present.");
      assertTrue(result.isOptionPresent(OPT_KEY1), "Second option not present.");
      assertTrue(result.isOptionPresent(OPT_KEY2), "Third option not present.");
   }

   @Test
   public void optionThatExpectsValueCannotBeInFrontInOneToken() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .buildOption(OPT_KEY0)
                                                      .setExpectsValue(true)
                                                      .build()
                                                   .addOption(OPT_KEY1)
                                                   .buildAndGet();

      assertThrows(InvalidOptionFormatException.class, () -> parser.parseArguments("-" + OPT_KEY0 + OPT_LONG_KEY1, OPT_VALUE0));
   }

   @Test
   public void specialOptionsArePresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .addOption(ExpectedOption.NO_KEY, "minus")
                                                   .addOption('-', "minusminus")
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, "-", "--");

      assertTrue(result.isOptionPresent(ExpectedOption.NO_KEY), "- option not present.");
      assertTrue(result.isOptionPresent('-'), "-- option not present");
   }

   @Test
   public void allPossibleOptionsArePresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .addOption(OPT_KEY0)
                                                   .addOption(OPT_LONG_KEY1)
                                                   .buildOption(OPT_KEY2)
                                                      .setExpectsValue(true)
                                                      .build()
                                                   .buildOption("long")
                                                      .setExpectsValue(true)
                                                      .build()
                                                   .buildOption('l')
                                                      .setLongKey("last")
                                                      .setExpectsValue(true)
                                                      .build()
                                                   .addOption('a')
                                                   .addOption('b')
                                                   .buildOption('c')
                                                      .setExpectsValue(true)
                                                      .setLongKey("cee")
                                                      .build()
                                                   .addOption(ExpectedOption.NO_KEY, "Minus")
                                                   .addOption('-', "MinusMinus")
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, "-" + OPT_KEY0,
                                           "--" + OPT_LONG_KEY1,
                                           "-" + OPT_KEY2, OPT_VALUE2,
                                           "--long=longValue",
                                           "-l", "lastValue",
                                           "-abc",
                                           "ceeValue",
                                           "-",
                                           "--");

      assertTrue(result.isOptionPresent(OPT_KEY0), "First provided option not present.");
      assertTrue(result.isOptionPresent(OPT_LONG_KEY1), "Second provided long option not present.");
      assertTrue(result.isOptionPresent(OPT_KEY2), "Third provided option not present.");
      assertEquals(OPT_VALUE2, result.getOptionValue(OPT_KEY2));
      assertTrue(result.isOptionPresent("long"), "Fourth provided option not present.");
      assertEquals("longValue", result.getOptionValue("long"));
      assertTrue(result.isOptionPresent("last"), "Fifth provided option not present.");
      assertEquals("lastValue", result.getOptionValue("last"));
      assertTrue(result.isOptionPresent('a'), "Option A in one token with b and c not present.");
      assertTrue(result.isOptionPresent('b'), "Option B in one token with a and c not present.");
      assertTrue(result.isOptionPresent('c'), "Option C in one token with a and b not present.");
      assertEquals("ceeValue", result.getOptionValue('c'));
      assertTrue(result.isOptionPresent(ExpectedOption.NO_KEY), "Minus option not present.");
      assertTrue(result.isOptionPresent('-'), "Minus minus option not present.");
   }

   @Test
   public void optionKeyAfterMinusMinusIsNotTreatedAsOption() throws MissingArgumentException {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .ignoreUnknown()
                                                   .addArgument(ARG_NAME0)
                                                   .addArgument(ARG_NAME1)
                                                   .buildArgument("Minus")
                                                      .setMandatory(true)
                                                      .build()
                                                   .addOption(OPT_KEY0)
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, ARG_VALUE0, "--", ARG_VALUE1, "-8", "-" + OPT_KEY0);
      assertEquals(ARG_VALUE0, result.getArgumentValue(ARG_NAME0));
      assertEquals(ARG_VALUE1, result.getArgumentValue(ARG_NAME1));
      assertEquals("-8", result.getArgumentValue("Minus"));
      assertFalse(result.isOptionPresent(OPT_KEY0));

      assertThrows(MissingArgumentException.class, () -> {
         parser.parseArguments(ARG_VALUE0, ARG_VALUE1, "-8", "-" + OPT_KEY0);
      });

   }

   @Test
   public void argsAndOptionsArePresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .addArgument(ARG_NAME0)
                                                   .addArgument(ARG_NAME1)
                                                   .buildOption(OPT_KEY0)
                                                      .setExpectsValue(true)
                                                      .build()
                                                   .addOption(OPT_KEY1)
                                                   .addOption(OPT_KEY2)
                                                   .buildAndGet();

      ArgumentModel result = parse(parser,
                                   "-" + OPT_KEY0,
                                   OPT_VALUE0,
                                   ARG_VALUE0,
                                   "-" + OPT_KEY1,
                                   ARG_VALUE1);

      Iterator<String> iterator = result.iterator();
      assertEquals(ARG_VALUE0, iterator.next());
      assertEquals(ARG_VALUE0, result.getArgumentValue(ARG_NAME0));
      assertEquals(ARG_VALUE1, iterator.next());
      assertEquals(ARG_VALUE1, result.getArgumentValue(ARG_NAME1));
      assertTrue(result.isOptionPresent(OPT_KEY0));
      assertEquals(OPT_VALUE0, result.getOptionValue(OPT_KEY0));
      assertTrue(result.isOptionPresent(OPT_KEY1));
      assertFalse(result.isOptionPresent(OPT_KEY2));
   }

   @Test
   public void testSimpleCommand() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .buildCommand(CMD_NAME0)
                                                      .build()
                                                   .buildAndGet();
      ArgumentModel result = parse(parser, CMD_NAME0);
      assertTrue(result.isCommandPresent(CMD_NAME0));
   }

   @Test
   public void testComplexCommand() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .buildCommand(CMD_NAME0)
                                                      .buildParser()
                                                         .addArgument(ARG_NAME0)
                                                         .addArgument(ARG_NAME1)
                                                         .buildOption(OPT_KEY0)
                                                            .setExpectsValue(true)
                                                            .build()
                                                         .addOption(OPT_KEY1)
                                                         .addOption(OPT_KEY2)
                                                         .build()
                                                      .build()
                                                   .buildAndGet();

      ArgumentModel result = parse(parser,
                                   CMD_NAME0,
                                   "-" + OPT_KEY0,
                                   OPT_VALUE0,
                                   ARG_VALUE0,
                                   "-" + OPT_KEY1,
                                   ARG_VALUE1);

      assertTrue(result.isCommandPresent(CMD_NAME0));
      ArgumentModel cmdResult = result.getCommandValue(CMD_NAME0);
      Iterator<String> iterator = cmdResult.iterator();
      assertEquals(ARG_VALUE0, iterator.next());
      assertEquals(ARG_VALUE0, cmdResult.getArgumentValue(ARG_NAME0));
      assertEquals(ARG_VALUE1, iterator.next());
      assertEquals(ARG_VALUE1, cmdResult.getArgumentValue(ARG_NAME1));
      assertTrue(cmdResult.isOptionPresent(OPT_KEY0));
      assertEquals(OPT_VALUE0, cmdResult.getOptionValue(OPT_KEY0));
      assertTrue(cmdResult.isOptionPresent(OPT_KEY1));
      assertFalse(cmdResult.isOptionPresent(OPT_KEY2));
   }

   @Test
   public void additionalAlternativeCommandThrowsException() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .buildCommand(CMD_NAME0)
                                                      .build()
                                                   .buildCommand(CMD_NAME1)
                                                      .build()
                                                   .buildAndGet();

      assertThrows(UnknownArgumentException.class, () -> parser.parseArguments(CMD_NAME0, CMD_NAME1));
   }

   @Test
   public void additionalAlternativeCommandNotPresent() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .setIgnoreUnknown(true)
                                                   .buildCommand(CMD_NAME0)
                                                      .build()
                                                   .buildCommand(CMD_NAME1)
                                                      .build()
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, CMD_NAME0, CMD_NAME1);

      assertTrue(result.getCommandValue(CMD_NAME0) != null, "First command should be present");
      assertTrue(result.getCommandValue(CMD_NAME1) == null, "Second command should not be present");
   }

   @Test
   public void additionalAlternativeCommandIsTreatedAsPlainArgument() {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .buildCommand(CMD_NAME0)
                                                      .build()
                                                   .buildCommand(CMD_NAME1)
                                                      .build()
                                                   .addArgument(ARG_NAME0)
                                                   .buildAndGet();

      ArgumentModel result = parse(parser, CMD_NAME1, CMD_NAME0);

      assertTrue(result.getCommandValue(CMD_NAME1) != null, "Command should be present.");
      assertEquals(CMD_NAME0, result.getArgumentValue(ARG_NAME0));
   }

   @Test
   public void syntaxIsCreatedCorrectly() {
      String expected = String.format("<%s> [<%s>] [-%s <%s>] [--%s] [--%s=<%s>]",
                                      ARG_NAME0,
                                      ARG_NAME1,
                                      OPT_KEY0,
                                      "loong",
                                      OPT_LONG_KEY1,
                                      OPT_LONG_KEY2,
                                      OPT_LONG_KEY2);

      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .addArgument(ARG_NAME0)
                                                   .buildArgument(ARG_NAME1)
                                                      .setMandatory(false)
                                                      .build()
                                                   .buildOption(OPT_KEY0)
                                                      .setExpectsValue(true)
                                                      .setLongKey("loong")
                                                      .build()
                                                   .addOption(OPT_LONG_KEY1)
                                                   .buildOption(OPT_LONG_KEY2)
                                                      .setExpectsValue(true)
                                                      .build()
                                                   .buildAndGet();
      assertEquals(expected, parser.syntax());
   }

   @Test
   public void ExceptionIsThrownWhenMissingAMandatoryArgument() throws MissingArgumentException {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .addArgument(ARG_NAME0)
                                                   .addArgument(ARG_NAME1)
                                                   .buildAndGet();

      assertThrows(MissingArgumentException.class, () -> parser.parseArguments(ARG_VALUE0));
   }

   @Test
   public void exceptionIsThrownWhenTooManyArgumentsAreGiven() throws UnknownArgumentException {
      ArgumentParser parser = ArgumentParserBuilder.begin()
                                                   .addArgument(ARG_NAME0)
                                                   .buildAndGet();

      assertThrows(UnknownArgumentException.class, () -> parser.parseArguments(ARG_VALUE0, "InvalidArg"));
   }

}
