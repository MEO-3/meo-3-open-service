---
name: NodeRED
colors:
  surface: '#faf9f9'
  surface-dim: '#dbdad9'
  surface-bright: '#faf9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f5f3f3'
  surface-container: '#efeded'
  surface-container-high: '#e9e8e8'
  surface-container-highest: '#e3e2e2'
  on-surface: '#1b1c1c'
  on-surface-variant: '#5e3f3a'
  inverse-surface: '#303031'
  inverse-on-surface: '#f2f0f0'
  outline: '#926e69'
  outline-variant: '#e8bdb6'
  surface-tint: '#c00000'
  primary: '#9e0000'
  on-primary: '#ffffff'
  primary-container: '#cc0000'
  on-primary-container: '#ffdad4'
  inverse-primary: '#ffb4a8'
  secondary: '#5d5f5f'
  on-secondary: '#ffffff'
  secondary-container: '#dcdddd'
  on-secondary-container: '#5f6161'
  tertiary: '#4c4c4c'
  on-tertiary: '#ffffff'
  tertiary-container: '#656464'
  on-tertiary-container: '#e4e1e1'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdad4'
  primary-fixed-dim: '#ffb4a8'
  on-primary-fixed: '#410000'
  on-primary-fixed-variant: '#930000'
  secondary-fixed: '#e2e2e2'
  secondary-fixed-dim: '#c6c6c7'
  on-secondary-fixed: '#1a1c1c'
  on-secondary-fixed-variant: '#454747'
  tertiary-fixed: '#e4e2e2'
  tertiary-fixed-dim: '#c8c6c6'
  on-tertiary-fixed: '#1b1c1c'
  on-tertiary-fixed-variant: '#474747'
  background: '#faf9f9'
  on-background: '#1b1c1c'
  surface-variant: '#e3e2e2'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 18px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  code-md:
    fontFamily: JetBrains Mono
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 20px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  base: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  gutter: 16px
  canvas-grid: 20px
---

## Brand & Style
The design system moves away from dark-themed industrial aesthetics toward a professional, high-clarity workspace. It targets developers, engineers, and automation specialists who require a focused environment for logic-building and flow orchestration.

The visual style is **Corporate / Modern** with a lean towards **Minimalism**. By using a pure white workspace (#ffffff) as the canvas, the primary brand red acts as a high-visibility functional signifier rather than just decoration. The emotional response should be one of "controlled complexity"—where the interface feels light and unobtrusive, allowing the user's logic and data to take center stage.

## Colors
The palette is intentionally restrained to maximize the impact of functional states.

*   **Primary (#cc0000):** Reserved for primary actions, active node states, and critical alerts. It must be used sparingly to prevent visual fatigue.
*   **Secondary (#f3f3f3):** Used for large surface areas like sidebars, node configuration panels, and the canvas background grid.
*   **Neutral / Surface:** Pure white (#ffffff) is the primary surface color for nodes and cards to ensure maximum contrast for text and connection wires.
*   **Typography & Borders:** A range of grays from #444444 (Text) to #dddddd (Borders) maintains a technical, structured feel without the harshness of pure black.

## Typography
This design system utilizes **Inter** for all UI elements to maintain a systematic, utilitarian feel. The hierarchy is tight and disciplined.

For technical contexts such as Function Nodes or JSON editors, **JetBrains Mono** is introduced to provide the necessary clarity for debugging and code authorship. All headings use a slightly tighter letter-spacing to maintain a modern, "engineered" look. Label styles are frequently used for node categories and property headers, often in uppercase with slight tracking.

## Layout & Spacing
The layout follows a **Fluid Grid** model with a hard 4px baseline rhythm.

1.  **The Canvas:** Uses a 20px dot-grid background for node alignment.
2.  **Panels:** Sidebars (Palette and Info/Debug) have a fixed width of 280px–320px to preserve vertical scanning, while the central workspace remains fluid.
3.  **Density:** Because this is a utility-heavy application, spacing is compact. Padding within nodes and list items should favor 8px (sm) and 12px increments to maximize information density without clutter.

## Elevation & Depth
Depth is communicated through **Low-contrast outlines** and subtle **Tonal layers** rather than heavy shadows.

*   **Level 0 (Canvas):** The #f3f3f3 background provides the base.
*   **Level 1 (Nodes/Cards):** White (#ffffff) surfaces with a 1px solid border (#dddddd).
*   **Level 2 (Dropdowns/Modals):** Pure white with a subtle, 8px blur, 10% opacity black shadow to lift the element off the technical grid.
*   **Active State:** When a node is selected, its border-width increases or changes to the primary red (#cc0000) to show focus without changing the layout.

## Shapes
The shape language is **Soft** but disciplined.

A corner radius of 4px (`rounded-sm`) is the standard for nodes, input fields, and buttons. This provides a clean, modern look that still feels architectural and precise. Larger containers like modals may use 8px (`rounded-lg`), but the overall aesthetic avoids "bubbly" or overly rounded elements to maintain its professional character.

## Components
Consistent styling across the utility suite:

*   **Buttons:** Primary buttons are solid #cc0000 with white text. Secondary buttons use a white fill with a #dddddd border and #444444 text.
*   **Nodes:** The core component. A 1px border, white background, and a color-coded "left-cap" indicating the node category.
*   **Input Fields:** Flat #ffffff background, 1px #dddddd border. On focus, the border transitions to #cc0000 with no outer glow.
*   **Chips:** Used for tags or node status. Light gray #eeeeee backgrounds with small 11px Inter Medium text.
*   **Lists (Debug/Palette):** Clean, borderless rows with an #f9f9f9 hover state. Use tight 4px vertical padding for high-density data.
*   **Tabs:** Minimalist underline style. Active tabs use the primary red for the underline (2px thick), inactive tabs are medium gray.