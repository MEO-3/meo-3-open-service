---
name: MEO 3 
colors:
  surface: '#f9f9f9'
  surface-dim: '#dadada'
  surface-bright: '#f9f9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f3'
  surface-container: '#eeeeee'
  surface-container-high: '#e8e8e8'
  surface-container-highest: '#e2e2e2'
  on-surface: '#1a1c1c'
  on-surface-variant: '#3e484f'
  inverse-surface: '#2f3131'
  inverse-on-surface: '#f1f1f1'
  outline: '#6e7880'
  outline-variant: '#bdc8d0'
  surface-tint: '#00668a'
  primary: '#00668a'
  on-primary: '#ffffff'
  primary-container: '#29abe2'
  on-primary-container: '#003b53'
  inverse-primary: '#7bd0ff'
  secondary: '#416900'
  on-secondary: '#ffffff'
  secondary-container: '#b7f568'
  on-secondary-container: '#457000'
  tertiary: '#ad3300'
  on-tertiary: '#ffffff'
  tertiary-container: '#ff784b'
  on-tertiary-container: '#691c00'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#c4e7ff'
  primary-fixed-dim: '#7bd0ff'
  on-primary-fixed: '#001e2c'
  on-primary-fixed-variant: '#004c69'
  secondary-fixed: '#b7f568'
  secondary-fixed-dim: '#9cd84f'
  on-secondary-fixed: '#102000'
  on-secondary-fixed-variant: '#304f00'
  tertiary-fixed: '#ffdbd0'
  tertiary-fixed-dim: '#ffb59e'
  on-tertiary-fixed: '#3a0b00'
  on-tertiary-fixed-variant: '#842500'
  background: '#f9f9f9'
  on-background: '#1a1c1c'
  surface-variant: '#e2e2e2'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
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
  label-mono:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.05em
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
  margin-mobile: 16px
  margin-desktop: 32px
---

## Brand & Style
The MEO 3 Terminal system is a sophisticated fusion of **Cyber-Brutalism** and **Modern Developer Tooling**. It evokes a high-stakes, technical environment where precision and system integrity are paramount. The brand personality is authoritative, transparent (showing the "under the hood" logs), and urgent.

The aesthetic utilizes a "Terminal-in-a-Box" metaphor, featuring heavy borders, monospaced data readouts, and a crisp, high-clarity light-mode interface. It is designed for developers, system architects, and technical operators who value functional density and structural clarity over decorative fluff.

## Colors
The palette is rooted in a clean, "technical laboratory" light mode.
- **Primary (#29abe2):** Used for interactive elements, terminal prompts, and successful system states.
- **Surface Palette:** Employs a range of cool grays and off-whites (`#f3f3f3` to `#e1e1e1`) to create structural separation through tonal shifts.
- **Semantic Accents:** High-fidelity colors are reserved for status logic:
    - **Error:** Strong reds (`#ba1a1a`) for fatal logs and breadcrumb alerts.
    - **Warning:** Warm oranges (`#f15a24`) for non-breaking system notes.
    - **Success/Secondary:** Organic greens (`#8cc63f`) for active system nodes.

## Typography
The system uses a dual-font approach.
- **Inter** handles all UI chrome, headlines, and primary communication. It provides a clean, modern, and highly legible foundation for the interface.
- **JetBrains Mono** is used for all "system data," including breadcrumbs, logs, terminal paths, and button labels. This creates a clear distinction between the "Application UI" and the "Engine Data."

Headlines should be used sparingly, often in uppercase with wide tracking to emphasize the industrial feel.

## Layout & Spacing
The layout follows a **Fixed-Width Container** model for main content, centered within a fluid viewport.

- **Grid:** A 12-column grid is implied for complex dashboards, but the primary view relies on vertical stacking with consistent padding (`p-md` on mobile, `p-xl` on desktop).
- **Terminal metaphor:** Use an 8px (sm) and 16px (md) spacing rhythm to keep elements tightly packed, reflecting the density of a command-line interface.
- **Decorative Elements:** An underlying 32px radial grid overlay (3% opacity) reinforces the technical precision of the layout.

## Elevation & Depth
Depth is communicated through **Tonal Layering** and **Subtle Outlines** rather than traditional shadows.

1.  **Background:** The base layer is `#f3f3f3`.
2.  **Containers:** Primary surfaces use white or `#ededed` with a subtle `#c0c8cd` (50% opacity) border.
3.  **Inset Surfaces:** Code blocks and log windows use `#e1e1e1` to appear recessed into the terminal.
4.  **Floating Elements:** While shadows are generally avoided, a `shadow-2xl` is used on the *main* terminal container to lift it off the background grid. All internal components remain flat.

## Shapes
The system uses a **Soft-Industrial** shape language.
- **Default (0.125rem):** Used for sharp technical details like system tags or tight-fitting status indicators.
- **Large (0.25rem):** Standard for buttons and inner container panels.
- **XL (0.5rem):** Used for primary card containers and log windows.
- **Full (0.75rem):** Used for the main terminal window only.

This hierarchy ensures that larger structural pieces feel approachable, while internal components feel precise and "tooled."

## Components
- **Buttons:** Use `JetBrains Mono`. Primary actions are solid (`primary-container`), while secondary navigation items use an outlined style with a subtle background fill. Always use uppercase for primary actions.
- **Terminal Log:** A recessed container with a custom scrollbar (using `outline-variant` colors). Includes a blinking cursor (`animate-blink`) to indicate the system is active.
- **Breadcrumbs:** Monospaced text separated by `chevron_right` icons. Status-specific colors (e.g., Error red) should be applied to the final crumb to indicate the current state.
- **Title Bar:** A distinct header with "traffic light" controls (Error, Warning, Success colored circles) to mimic the OS terminal window experience.
- **Quick Recovery Chips:** Outlined buttons that act as a sitemap, using `primary` text and `surface-container` backgrounds to maintain high visibility without being overwhelming.