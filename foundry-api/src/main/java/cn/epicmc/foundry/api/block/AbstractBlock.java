package cn.epicmc.foundry.api.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.Objects;

/**
 * Abstract base implementation of {@link BlockDefinition}.
 * <p>
 * Provides common functionality for block definitions with sensible defaults.
 * Third-party plugins can extend this class to create custom blocks.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * public class GeneratorBlock extends AbstractBlock {
 *
 *     public GeneratorBlock() {
 *         super(
 *             new NamespacedKey("example", "generator"),
 *             Material.NOTE_BLOCK,
 *             "Electric Generator"
 *         );
 *     }
 *
 *     @Override
 *     public boolean isMachine() {
 *         return true;
 *     }
 *
 *     @Override
 *     public BlockFactory getFactory() {
 *         return (def, loc, ctx) -> new GeneratorInstance(loc);
 *     }
 * }
 * }</pre>
 */
public abstract class AbstractBlock implements BlockDefinition {

    private final NamespacedKey id;
    private final Material baseMaterial;
    private final String displayName;
    private final float hardness;
    private final float blastResistance;

    /**
     * Create a new block definition with default properties.
     *
     * @param id The unique identifier
     * @param baseMaterial The Minecraft material to use
     * @param displayName The display name
     */
    protected AbstractBlock(
        NamespacedKey id,
        Material baseMaterial,
        String displayName
    ) {
        this(id, baseMaterial, displayName, 5.0f, 6.0f);
    }

    /**
     * Create a new block definition with custom properties.
     *
     * @param id The unique identifier
     * @param baseMaterial The Minecraft material to use
     * @param displayName The display name
     * @param hardness The hardness value
     * @param blastResistance The blast resistance value
     */
    protected AbstractBlock(
        NamespacedKey id,
        Material baseMaterial,
        String displayName,
        float hardness,
        float blastResistance
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.baseMaterial = Objects.requireNonNull(baseMaterial, "baseMaterial");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.hardness = hardness;
        this.blastResistance = blastResistance;

        // Validate material is a block
        if (!baseMaterial.isBlock()) {
            throw new IllegalArgumentException(
                "Base material must be a block: " + baseMaterial
            );
        }
    }

    @Override
    public NamespacedKey getId() {
        return id;
    }

    @Override
    public Material getBaseMaterial() {
        return baseMaterial;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public float getHardness() {
        return hardness;
    }

    @Override
    public float getBlastResistance() {
        return blastResistance;
    }

    @Override
    public boolean isMachine() {
        return false;
    }

    @Override
    public BlockFactory getFactory() {
        return null;
    }

    @Override
    public String toString() {
        return "Block{" + id + ", " + displayName + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockDefinition other)) return false;
        return id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

