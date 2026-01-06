package pion.datlt.libads.iap.di

/**
 * Service locator for IAP dependencies.
 * Provides global access to initialized dependencies while maintaining singleton pattern.
 */
object IapProvider {
    private var dependencies: IapDependencies? = null

    /**
     * Initialize the provider with dependencies.
     * @param deps The dependencies container from the factory
     */
    fun initialize(deps: IapDependencies) {
        dependencies = deps
    }

    /**
     * Get current dependencies.
     * @return IapDependencies if initialized, null otherwise
     */
    fun getDependencies(): IapDependencies? = dependencies

    /**
     * Clear all dependencies and reset state.
     * Should be called when IAP is released.
     */
    fun clear() {
        dependencies = null
    }

    /**
     * Check if dependencies are initialized.
     */
    fun isInitialized(): Boolean = dependencies != null
}
