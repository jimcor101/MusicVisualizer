# Music Visualizer

A JavaFX-based music visualizer application that creates stunning dynamic visual effects synchronized to audio playback. Features real-time spectrum analysis, multiple visualization modes, and interactive controls.

![Music Visualizer Demo](https://img.shields.io/badge/JavaFX-21.0.2-blue) ![Java](https://img.shields.io/badge/Java-17+-orange) ![Maven](https://img.shields.io/badge/Maven-3.6+-green)

## Features

### 🎨 Eight Visualization Modes

1. **Spectrum Bars** - Classic frequency spectrum bars with waveform overlay (default)
2. **Particles** - Dynamic particle system responding to audio intensity
3. **Circular** - Radial frequency visualization in circular pattern
4. **Waveform** - Scrolling waveform display with color-coded history trails
5. **3D Spectrum** - Frequency bars with perspective depth effects
6. **Oscilloscope** - Classic circular audio scope patterns with trails
7. **Mandala** - 8-fold symmetric kaleidoscope patterns
8. **Star Field** - Bass-responsive star particles with constellation effects

### 🎵 Audio Features

- **Real-time spectrum analysis** (64 to 256 frequency bands)
- **Multi-format support** (MP3, WAV, M4A)
- **Audio controls** (play/pause, volume, seek bar)
- **Beat detection** with synchronized visual effects
- **Smooth interpolation** and filtering of audio data

### 🖥️ User Interface

- **Responsive layout** with control panel and optional settings panel
- **Fullscreen mode** with auto-hide controls for immersive experience
- **Dropdown mode selector** for easy visualization switching
- **Adjustable parameters** (smoothing factor, particle count)
- **Keyboard shortcuts** for quick mode switching and controls

## Screenshots

*Screenshots would go here showing different visualization modes*

## Requirements

- **Java 17+**
- **JavaFX 21+** (for macOS compatibility)
- **Maven 3.6+**

## Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/jimcor101/MusicVisualizer.git
   cd MusicVisualizer
   ```

2. **Compile the project:**
   ```bash
   mvn clean compile
   ```

3. **Run the application:**
   ```bash
   ./run.sh
   ```

   Or alternatively:
   ```bash
   mvn javafx:run
   ```

## Usage

### Getting Started

1. Launch the application
2. Select an audio file (MP3, WAV, or M4A) when prompted
3. The visualizer will start with the default Spectrum Bars mode
4. Use controls to switch modes and adjust settings

### Controls

#### Keyboard Shortcuts
- **1-8** - Switch between visualization modes
- **Space** - Toggle play/pause
- **F11** - Toggle fullscreen mode
- **Ctrl+S** - Toggle settings panel
- **Escape** - Exit fullscreen or quit application

#### UI Controls
- **Play/Pause Button** - Control audio playback
- **Volume Slider** - Adjust audio volume (0-100%)
- **Seek Bar** - Navigate through the audio track
- **Mode Dropdown** - Select visualization mode
- **Settings Panel** - Adjust smoothing factor and particle count

### Visualization Modes

#### Spectrum Bars (Mode 1)
Classic frequency spectrum display with colored bars representing different frequency bands. Includes waveform overlay for enhanced visual appeal.

#### Particles (Mode 2)
Dynamic particle system that spawns particles based on audio intensity. Particles have physics-based movement and fade over time.

#### Circular (Mode 3)
Radial spectrum visualization arranged in a circular pattern. Creates mesmerizing circular waves synchronized to the music.

#### Waveform (Mode 4)
Displays scrolling waveform history with color-coded trails showing the evolution of the audio signal over time.

#### 3D Spectrum (Mode 5)
Enhanced spectrum bars with perspective depth effects, creating a three-dimensional appearance with vanishing point perspective.

#### Oscilloscope (Mode 6)
Classic circular oscilloscope visualization with trailing effects, mimicking traditional audio equipment displays.

#### Mandala (Mode 7)
Beautiful 8-fold symmetric patterns that create kaleidoscope effects. The patterns rotate and pulse in response to the music.

#### Star Field (Mode 8)
Bass-responsive star particles that pulse, move, and form constellation lines based on low-frequency audio content.

## Configuration

The application supports various customizable parameters through the settings panel:

- **Smoothing Factor** (0.1-0.95): Controls how much the spectrum data is smoothed between frames
- **Particle Count** (1-50): Number of particles spawned per frame in particle mode
- **Beat Sensitivity**: Automatically adjusts based on audio content

## Development

### Project Structure
```
MusicVisualizer/
├── src/main/java/com/musicvisualizer/
│   └── Visualizer.java           # Main application class
├── target/                       # Maven build output
├── .settings/                    # Eclipse project settings
├── pom.xml                       # Maven configuration
├── run.sh                        # Execution script
├── run-debug.sh                  # Debug execution script
├── CLAUDE.md                     # Development documentation
└── README.md                     # This file
```

### Building from Source

```bash
# Clean and compile
mvn clean compile

# Run tests (if any)
mvn test

# Package as JAR
mvn package

# Run with debugging
./run-debug.sh
```

### Code Architecture

The application uses a single-class architecture with embedded components:

- **Main Class**: `Visualizer` extends JavaFX Application
- **Audio Processing**: Real-time spectrum analysis with MediaPlayer
- **Visualization Engine**: Canvas-based rendering with multiple mode support
- **UI Framework**: BorderPane layout with responsive controls
- **Beat Detection**: Energy-based analysis for visual synchronization

## Technical Details

- **Canvas Size**: Responsive (1000x700 default)
- **Audio Bands**: 64 input bands interpolated to 256 output bands
- **Rendering**: 60 FPS using JavaFX AnimationTimer
- **Color System**: HSB color space for vibrant visualizations
- **Audio Processing**: 20ms spectrum interval with -60dB threshold filtering

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Built with JavaFX for cross-platform compatibility
- Inspired by classic music visualizers and Winamp plugins
- Uses advanced audio spectrum analysis for real-time visualization

## Troubleshooting

### Common Issues

**Application won't start:**
- Ensure Java 17+ is installed
- Verify JavaFX 21+ is available
- Check that audio file formats are supported

**No audio visualization:**
- Verify audio file is not corrupted
- Check system audio output
- Try different audio file formats

**Poor performance:**
- Reduce smoothing factor in settings
- Lower particle count for particle mode
- Close other resource-intensive applications

### Support

For issues, feature requests, or questions:
- Open an issue on GitHub
- Check the CLAUDE.md file for development details
- Review the troubleshooting section above

---

**Version**: 2.0.0  
**Author**: Claude AI Assistant  
**Repository**: https://github.com/jimcor101/MusicVisualizer