/**
 * Unit system for the Foundry framework.
 * <p>
 * This package provides a comprehensive unit system for measuring various quantities
 * in the technology framework. All units are strictly typed and have explicit conversions.
 *
 * <h2>Design Principles</h2>
 * <ul>
 *   <li><b>Explicit Units:</b> All numeric values have a clearly defined unit</li>
 *   <li><b>Standard Units:</b> Each category has a standard unit for internal calculations</li>
 *   <li><b>Type Safety:</b> Energy, fluids, temperature, etc. use different types</li>
 *   <li><b>No Automatic Conversion:</b> Conversions must be explicit</li>
 * </ul>
 *
 * <h2>Standard Units</h2>
 * <table border="1">
 *   <tr>
 *     <th>Category</th>
 *     <th>Standard Unit</th>
 *     <th>Symbol</th>
 *     <th>Usage</th>
 *   </tr>
 *   <tr>
 *     <td>Energy</td>
 *     <td>Electric Unit</td>
 *     <td>EU</td>
 *     <td>Internal storage: {@code double}</td>
 *   </tr>
 *   <tr>
 *     <td>Fluid</td>
 *     <td>Millibucket</td>
 *     <td>mB</td>
 *     <td>Internal storage: {@code long} (1000 mB = 1 bucket)</td>
 *   </tr>
 *   <tr>
 *     <td>Temperature</td>
 *     <td>Kelvin</td>
 *     <td>K</td>
 *     <td>Internal storage: {@code double}</td>
 *   </tr>
 *   <tr>
 *     <td>Pressure</td>
 *     <td>Pascal</td>
 *     <td>Pa</td>
 *     <td>Internal storage: {@code double}</td>
 *   </tr>
 * </table>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Working with Energy</h3>
 * <pre>{@code
 * // Define energy type
 * EnergyUnit eu = StandardUnits.EU;
 *
 * // Energy storage uses standard unit (EU)
 * double stored = 1000.0; // 1000 EU
 *
 * // Check compatibility before transfer
 * if (source.getEnergyType().isCompatibleWith(target.getEnergyType())) {
 *     target.insert(eu, stored, false);
 * }
 * }</pre>
 *
 * <h3>Working with Fluids</h3>
 * <pre>{@code
 * // All fluid amounts are in millibuckets (mB)
 * long amount = 1000L; // 1000 mB = 1 bucket
 *
 * // Convert for display
 * FluidUnit bucket = StandardUnits.BUCKET;
 * double buckets = bucket.fromMillibuckets(amount); // 1.0 buckets
 *
 * // Create fluid stack
 * FluidStack water = FluidStack.of(waterType, amount);
 * }</pre>
 *
 * <h3>Working with Temperature</h3>
 * <pre>{@code
 * // Internal temperature is always in Kelvin
 * double tempK = 373.15; // 373.15 K (100°C)
 *
 * // Convert for display
 * TemperatureUnit celsius = StandardUnits.CELSIUS;
 * double tempC = celsius.fromKelvin(tempK); // 100.0°C
 *
 * // Validate temperature
 * if (celsius.isValid(tempC)) {
 *     machine.setTemperature(celsius.toKelvin(tempC));
 * }
 * }</pre>
 *
 * <h3>Working with Pressure</h3>
 * <pre>{@code
 * // Internal pressure is always in Pascals
 * double pressurePa = 101325.0; // 1 atm
 *
 * // Convert for display
 * PressureUnit atm = StandardUnits.ATMOSPHERE;
 * double pressureAtm = atm.fromPascals(pressurePa); // 1.0 atm
 *
 * // Check pressure limits
 * if (pressurePa > tank.getMaxPressure()) {
 *     tank.explode();
 * }
 * }</pre>
 *
 * <h3>Registering Custom Units</h3>
 * <pre>{@code
 * // Register custom energy unit
 * public class MyEnergyUnit implements EnergyUnit {
 *     private static final NamespacedKey ID =
 *         new NamespacedKey("myplugin", "my_energy");
 *
 *     @Override
 *     public NamespacedKey getId() {
 *         return ID;
 *     }
 *
 *     @Override
 *     public String getSymbol() {
 *         return "ME";
 *     }
 *
 *     @Override
 *     public String getName() {
 *         return "My Energy";
 *     }
 * }
 *
 * // Register with Foundry
 * FoundryAPI.getRegistry(EnergyUnit.class)
 *     .register(myUnit.getId(), myUnit);
 * }</pre>
 *
 * <h2>Important Rules</h2>
 * <ol>
 *   <li>Always use standard units for internal calculations</li>
 *   <li>Only convert to other units for display purposes</li>
 *   <li>Never mix units of different categories</li>
 *   <li>Energy types with the same symbol from different plugins are NOT compatible</li>
 *   <li>Use {@code long} for fluid amounts to avoid floating-point errors</li>
 *   <li>Validate temperature (>= 0 K) and pressure (>= 0 Pa)</li>
 * </ol>
 *
 * @see cn.epicmc.foundry.api.unit.Unit
 * @see cn.epicmc.foundry.api.unit.UnitCategory
 * @see cn.epicmc.foundry.api.unit.EnergyUnit
 * @see cn.epicmc.foundry.api.unit.FluidUnit
 * @see cn.epicmc.foundry.api.unit.TemperatureUnit
 * @see cn.epicmc.foundry.api.unit.PressureUnit
 */
package cn.epicmc.foundry.api.unit;
