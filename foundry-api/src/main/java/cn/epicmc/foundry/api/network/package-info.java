/**
 * Network system for managing content transfer between nodes.
 * <p>
 * The network module provides:
 * <ul>
 *   <li>Unified topology management for energy, fluid, and item networks</li>
 *   <li>Automatic network discovery and connection</li>
 *   <li>Incremental network updates on topology changes</li>
 *   <li>Asynchronous transfers using virtual threads</li>
 *   <li>Thread-safe concurrent operations</li>
 * </ul>
 *
 * <h2>Architecture</h2>
 * <pre>
 * NetworkManager (manages all networks)
 *     ↓
 * Network (manages topology for one network)
 *     ↓
 * NetworkNode (represents a block in the network)
 *     ↓
 * TransferHandler (handles actual content transfer)
 * </pre>
 *
 * <h2>Thread Safety</h2>
 * All network operations are designed to be thread-safe:
 * <ul>
 *   <li>Read operations use concurrent collections</li>
 *   <li>Write operations use atomic updates and locks</li>
 *   <li>Expensive operations run on virtual threads</li>
 *   <li>Main thread is never blocked</li>
 * </ul>
 *
 * <h2>Performance</h2>
 * The network system is optimized for large-scale networks:
 * <ul>
 *   <li>Incremental topology updates (only affected nodes)</li>
 *   <li>Virtual threads for parallel processing</li>
 *   <li>Cached connections and paths</li>
 *   <li>Batched transfer operations</li>
 * </ul>
 *
 * @since 1.0.0
 */
package cn.epicmc.foundry.api.network;
