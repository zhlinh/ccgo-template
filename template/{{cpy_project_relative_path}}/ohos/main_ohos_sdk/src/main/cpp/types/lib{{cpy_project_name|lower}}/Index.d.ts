/**
 * {{cpy_project_name|capitalize}} Native Module
 *
 * This module provides TypeScript bindings for the {{cpy_project_name|lower}} native library.
 * It allows ArkTS/TypeScript code to call native C++ functions through NAPI.
 *
 * @packageDocumentation
 */

/**
 * Set debug log flag for the {{cpy_project_name|lower}} library.
 *
 * @param isDebugLog - Boolean flag to enable or disable debug logging
 * @returns void
 *
 * @example
 * ```typescript
 * import {{cpy_project_name|capitalize}} from 'lib{{cpy_project_name|lower}}.so';
 *
 * // Enable debug logging
 * {{cpy_project_name|capitalize}}.setDebugLog(true);
 *
 * // Disable debug logging
 * {{cpy_project_name|capitalize}}.setDebugLog(false);
 * ```
 */
export const setDebugLog: (isDebugLog: boolean) => void;
