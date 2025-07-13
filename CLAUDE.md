# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview
This is a JavaFX-based music visualizer application that creates dynamic visual effects synchronized to audio playback. The project contains a single main class `Visualizer` that implements an audio spectrum analyzer with multiple visualization modes, audio controls, settings panel, and beat detection.

## Development Commands
This is a Maven-managed Java project with complete Eclipse IDE integration:

```bash
# Compile the project
mvn clean compile

# Run the application (recommended)
./run.sh

# Alternative: Run with Maven
mvn exec:java -Dexec.mainClass="com.musicvisualizer.Visualizer"

# Run in Eclipse using the provided launch configuration
# File -> Import -> Run/Debug -> Launch Configurations -> Visualizer.launch
```

**Important**: This project requires Java 17+ and JavaFX 21+ with media module support. JavaFX 21+ resolves macOS compatibility issues present in earlier versions.

## Architecture
The application follows a comprehensive single-class architecture with embedded components:

- **Main Class**: `Visualizer` extends JavaFX Application
- **Core Components**:
  - Audio spectrum analysis using JavaFX MediaPlayer with AudioSpectrumListener
  - Real-time visualization rendering with Canvas/GraphicsContext
  - Particle system for dynamic effects (inner `Particle` class)
  - Multiple visualization modes (spectrum bars, particles, circular)
  - Audio controls (play/pause, volume, seek)
  - Settings panel with customizable parameters
  - Beat detection with synchronized visual effects
  - Fullscreen mode with auto-hide controls

## Code Structure (Visualizer.java)
- **Constants**: Display dimensions, audio processing, particle system, modes (lines 77-123)
- **UI Components**: BorderPane layout, Canvas, control panels, sliders (lines 157-177)
- **Audio Processing**: `processWaveform()`, `interpolateAndFilterMagnitudes()` (lines 414-463)
- **Visualization Modes**: 
  - Spectrum bars: `drawSpectrum()` (lines 521-542)
  - Particles: `drawParticles()` (lines 508-513)
  - Circular: `drawCircularSpectrum()` (lines 184-217)
- **Beat Detection**: `updateBeatDetection()` with energy analysis (lines 218-244)
- **UI Creation**: `createUI()`, `createControlPanel()`, `createSettingsPanel()` (lines 135-183)
- **Control Functions**: Play/pause, fullscreen, settings toggle (lines 245-280)
- **Particle System**: `spawnParticles()`, `updateParticles()`, `Particle` class (lines 399-681)

## Key Features
- **Audio Processing**: 64-band input spectrum mapped to 256 output bands with interpolation
- **Visualization Modes**: 
  - Spectrum bars with waveform overlay (Mode 1)
  - Particle system mode (Mode 2)
  - Circular spectrum visualization (Mode 3)
- **Audio Controls**: Play/pause button, volume slider, seek bar
- **Settings Panel**: Smoothing factor and particle count sliders
- **Beat Detection**: Real-time energy analysis with visual flash effects
- **Fullscreen Mode**: F11 toggle with auto-hide controls
- **Keyboard Controls**: Number keys (1-3) for mode switching, Space for play/pause
- **Real-time Rendering**: Uses AnimationTimer for smooth 60fps rendering
- **File Support**: MP3, WAV, M4A files via FileChooser dialog

## Technical Details
- **Canvas Size**: Responsive (1000x700 default with 100px control panel)
- **Audio Bands**: 64 input bands (INPUT_BANDS) interpolated to 256 output bands (OUTPUT_BANDS)
- **Audio Processing**: 20ms spectrum interval, -60dB threshold filtering
- **Smoothing**: Adjustable smoothing factor (0.1-0.95, default 0.75) for spectrum data
- **Particle System**: 100-frame lifetime with 0.98 size decay per frame, adjustable count (1-50)
- **Beat Detection**: 43-sample energy history with 1.5x sensitivity multiplier
- **Color System**: HSB color space with time-based hue shifts and beat synchronization
- **Performance**: AnimationTimer for 60fps rendering, mode-based optimization
- **UI Layout**: BorderPane with bottom controls, optional right settings panel

## File Structure
```
MusicVisualizer/
├── src/main/java/com/musicvisualizer/
│   └── Visualizer.java           # Main application class
├── target/                       # Maven build output
├── .settings/                    # Eclipse project settings
├── .classpath                    # Eclipse classpath configuration
├── .project                      # Eclipse project file
├── pom.xml                       # Maven configuration
├── run.sh                        # Execution script
├── Visualizer.launch             # Eclipse launch configuration
└── CLAUDE.md                     # This documentation
```

## Dependencies (pom.xml)
- JavaFX 21.0.2 (controls, media, fxml modules)
- Java 17+ (Maven compiler plugin)
- Maven 3.6+ for build management

## Usage Notes
- Application requires audio file selection on startup via FileChooser
- **Keyboard Controls**:
  - `1`, `2`, `3`: Switch visualization modes
  - `Space`: Toggle play/pause
  - `F11`: Toggle fullscreen mode
  - `Ctrl+S`: Toggle settings panel
  - `Escape`: Exit fullscreen or quit application
- **UI Controls**:
  - Play/pause button in control panel
  - Volume slider (0-100%)
  - Mode selector dropdown
  - Settings panel with smoothing and particle count sliders
- **Beat Detection**: Automatically analyzes audio energy and enhances visuals
- **Fullscreen**: Hides all UI controls for immersive experience
- Error handling for invalid/missing files with user-friendly dialogs

## Common Development Tasks
When modifying this code:
- **Adding new visualizations**: Extend the `drawVisualizer()` switch statement with new modes
- **Audio format support**: Modify FileChooser extensions and test Media constructor compatibility
- **Performance tuning**: Adjust smoothing factors, particle counts, beat sensitivity, or rendering intervals
- **UI enhancements**: Add new controls to control panel or settings panel
- **Audio analysis**: Modify band counts, interpolation algorithms, threshold values, or beat detection parameters
- **Visual effects**: Enhance beat detection responses, add new color schemes, or particle behaviors
- **Keyboard shortcuts**: Extend `setupKeyHandling()` method with new key bindings

## Testing
Run the application and verify:
1. Audio file loading (MP3, WAV, M4A)
2. All three visualization modes (spectrum, particles, circular)
3. Audio controls (play/pause, volume, seeking)
4. Settings panel functionality
5. Beat detection visual responses
6. Fullscreen mode and control auto-hide
7. Keyboard shortcuts and mode switching
8. Proper cleanup on application exit