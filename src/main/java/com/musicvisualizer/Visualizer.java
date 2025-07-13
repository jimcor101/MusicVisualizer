/*
 * Copyright (c) 2025 Cornacchia Development, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Cornacchia Development, Inc. ("Confidential Information"). You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Cornacchia Development, Inc.
 *
 * File: Visualizer.java
 * Description: JavaFX-based music visualizer with spectrum analysis and particle effects
 * Author: Claude AI Assistant
 * Created: 2025
 * Last Modified: 2025
 */

package com.musicvisualizer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert;
import javafx.application.Platform;
import javafx.scene.media.MediaException;
import javafx.geometry.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * JavaFX-based music visualizer application that creates dynamic visual effects 
 * synchronized to audio playback. Features include real-time spectrum analysis, 
 * particle effects, and multiple visualization modes.
 * 
 * The application provides 8 visualization modes:
 * 1. Spectrum bars with waveform overlay (default)
 * 2. Particle system effects
 * 3. Circular spectrum visualization
 * 4. Waveform mode with scrolling history
 * 5. 3D Spectrum with perspective depth effects
 * 6. Oscilloscope mode with circular audio scope patterns
 * 7. Mandala/Kaleidoscope with 8-fold symmetric patterns
 * 8. Star Field with bass-responsive star particles
 * 
 * Key Features:
 * - Real-time audio spectrum analysis (64 to 256 bands)
 * - Smooth interpolation and filtering of audio data
 * - Dynamic particle system with decay effects
 * - HSB color space for vibrant visualizations
 * - Support for multiple audio formats (MP3, WAV, M4A)
 * - Keyboard controls for mode switching (1-8 keys) and exit
 * - Dropdown mode selector in UI
 * 
 * @author Claude AI Assistant
 * @version 2.0.0
 * @since 2025
 */
public class Visualizer extends Application {
    // Display Constants
    /** Application window width in pixels */
    private static final int WIDTH = 1000;
    /** Application window height in pixels */
    private static final int HEIGHT = 700;
    /** Control panel height */
    private static final int CONTROL_PANEL_HEIGHT = 100;
    /** Settings panel width */
    private static final int SETTINGS_PANEL_WIDTH = 250;
    
    // Audio Processing Constants
    /** Number of input frequency bands from MediaPlayer */
    private static final int INPUT_BANDS = 64;
    /** Number of output frequency bands for visualization */
    private static final int OUTPUT_BANDS = 256;
    /** Minimum decibel threshold for audio processing */
    private static final double DB_THRESHOLD = -60.0;
    /** Minimum height for spectrum bars */
    private static final double MIN_HEIGHT = 5;
    /** Default smoothing factor for spectrum data (0.0 to 1.0) */
    private static final double DEFAULT_SMOOTHING_FACTOR = 0.75;
    /** Audio spectrum update interval in seconds */
    private static final double SPECTRUM_INTERVAL_SECONDS = 0.02;
    
    // Particle System Constants
    /** Rate at which particle size decreases per frame */
    private static final double PARTICLE_SIZE_DECAY = 0.98;
    /** Number of frames a particle remains visible */
    private static final int PARTICLE_LIFETIME_FRAMES = 100;
    /** Minimum audio intensity required to spawn particles */
    private static final int PARTICLE_SPAWN_INTENSITY_THRESHOLD = 10;
    /** Default number of particles spawned per frame when threshold is met */
    private static final int DEFAULT_PARTICLE_SPAWN_COUNT = 10;
    
    // Visualization Mode Constants
    /** Spectrum bars visualization mode */
    private static final int MODE_SPECTRUM = 0;
    /** Particle system visualization mode */
    private static final int MODE_PARTICLES = 1;
    /** Circular spectrum visualization mode */
    private static final int MODE_CIRCULAR = 2;
    /** Waveform visualization mode */
    private static final int MODE_WAVEFORM = 3;
    /** 3D Spectrum visualization mode */
    private static final int MODE_3D_SPECTRUM = 4;
    /** Oscilloscope visualization mode */
    private static final int MODE_OSCILLOSCOPE = 5;
    /** Mandala/Kaleidoscope visualization mode */
    private static final int MODE_MANDALA = 6;
    /** Star Field visualization mode */
    private static final int MODE_STARFIELD = 7;
    
    // Beat Detection Constants
    /** Number of samples to analyze for beat detection */
    private static final int BEAT_HISTORY_SIZE = 43;
    /** Beat detection sensitivity multiplier */
    private static final double BEAT_SENSITIVITY = 1.5;
    
    // 3D Visualization Constants
    /** Perspective factor for 3D depth effects */
    private static final double PERSPECTIVE_FACTOR = 0.8;
    
    // Core Components
    /** JavaFX MediaPlayer for audio playback and spectrum analysis */
    private MediaPlayer mediaPlayer;
    /** Animation timer for rendering loop */
    private AnimationTimer animationTimer;
    
    // Audio Data Arrays
    /** Raw magnitude data from MediaPlayer */
    private float[] magnitudes = new float[INPUT_BANDS];
    /** Smoothed and interpolated magnitude data for rendering */
    private float[] smoothedMagnitudes = new float[OUTPUT_BANDS];
    /** Processed waveform points for overlay rendering */
    private float[] waveformPoints = new float[OUTPUT_BANDS];
    
    // Particle System
    /** List of active particles in the system */
    private List<Particle> particles = new ArrayList<>();
    /** Random number generator for particle effects */
    private Random random = new Random();
    
    // New Visualization Mode Data
    /** Waveform history for scrolling display (300 frames = 5 seconds at 60fps) */
    private float[][] waveformHistory = new float[300][OUTPUT_BANDS];
    /** Current index in waveform history circular buffer */
    private int waveformHistoryIndex = 0;
    /** Star particles for star field mode */
    private List<Star> stars = new ArrayList<>();
    /** Random number generator for star field effects */
    private Random starRandom = new Random();
    /** Previous audio sample for oscilloscope mode */
    private float previousSample = 0.0f;
    /** Trail points for oscilloscope visualization */
    private List<Point2D> scopeTrail = new ArrayList<>();
    
    // State Management
    /** Current visualization mode */
    private int visualizationMode = MODE_SPECTRUM;
    /** Current smoothing factor (adjustable) */
    private double currentSmoothingFactor = DEFAULT_SMOOTHING_FACTOR;
    /** Current particle spawn count (adjustable) */
    private int currentParticleCount = DEFAULT_PARTICLE_SPAWN_COUNT;
    /** Flag for fullscreen mode */
    private boolean isFullscreen = false;
    /** Flag for settings panel visibility */
    private boolean settingsVisible = false;
    /** Flag to prevent double cleanup */
    private boolean isCleanedUp = false;
    /** Flag to prevent volume feedback loops */
    private boolean isUpdatingVolume = false;
    /** Last volume update time for rate limiting */
    private long lastVolumeUpdateTime = 0;
    /** Minimum time between volume updates (milliseconds) */
    private static final long VOLUME_UPDATE_THROTTLE_MS = 50;
    /** Last time audio spectrum was logged */
    private long lastAudioLogTime = 0;
    
    // UI Components
    /** Main application layout */
    private BorderPane mainLayout;
    /** Canvas container */
    private StackPane canvasContainer;
    /** Control panel for audio controls */
    private VBox controlPanel;
    /** Settings panel for customization */
    private VBox settingsPanel;
    /** Play/pause button */
    private Button playPauseButton;
    /** Volume slider */
    private Slider volumeSlider;
    /** Seek bar for track position */
    private ProgressBar seekBar;
    /** Smoothing factor slider */
    private Slider smoothingSlider;
    /** Particle count slider */
    private Slider particleCountSlider;
    /** Visualization mode selector */
    private ComboBox<String> modeSelector;
    /** Canvas for rendering */
    private Canvas canvas;
    /** Graphics context for canvas */
    private GraphicsContext gc;
    
    // Beat Detection
    /** History of energy values for beat detection */
    private double[] energyHistory = new double[BEAT_HISTORY_SIZE];
    /** Current position in energy history buffer */
    private int energyHistoryIndex = 0;
    /** Flag indicating if a beat was detected this frame */
    private boolean beatDetected = false;
    /** Beat flash intensity (0.0 to 1.0) */
    private double beatFlashIntensity = 0.0;

    /**
     * Application entry point. Launches the JavaFX application.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * JavaFX Application start method. Initializes the application window,
     * sets up audio processing, and begins the visualization loop.
     * 
     * @param stage Primary stage for this application
     */
    @Override
    public void start(Stage stage) {
        System.out.println("[INIT] Starting Music Visualizer application");
        stage.setTitle("Music Visualizer - Enhanced Edition");
        
        // Set up proper shutdown handling
        stage.setOnCloseRequest(e -> {
            System.out.println("[SHUTDOWN] Application close requested");
            cleanup();
            Platform.exit();
        });

        System.out.println("[INIT] Requesting audio file selection");
        File selectedFile = selectAudioFile(stage);
        if (selectedFile == null) {
            System.out.println("[INIT] No file selected, exiting application");
            Platform.exit();
            return;
        }

        System.out.println("[INIT] Initializing media player");
        if (!initializeMediaPlayer(selectedFile)) {
            System.out.println("[ERROR] Media player initialization failed, exiting");
            Platform.exit();
            return;
        }

        System.out.println("[INIT] Creating UI components");
        try {
            // Create UI components
            createUI();
            
            System.out.println("[INIT] Setting up audio and animation systems");
            // Setup audio and animation
            setupAudioProcessing();
            setupAnimationTimer();
            setupMediaPlayerCallbacks();
            
            System.out.println("[INIT] Creating and showing main window");
            // Create and show scene
            Scene scene = new Scene(mainLayout, WIDTH, HEIGHT, Color.BLACK);
            setupKeyHandling(scene, stage);
            
            stage.setScene(scene);
            stage.show();
            System.out.println("[INIT] Application startup completed successfully");
        } catch (Exception e) {
            System.err.println("[ERROR] Fatal error during application initialization: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }
    
    /**
     * Creates the main UI layout with canvas, controls, and settings panel.
     */
    private void createUI() {
        System.out.println("[UI] Creating main UI layout");
        try {
            // Create main layout
            System.out.println("[UI] Creating BorderPane main layout");
            mainLayout = new BorderPane();
            
            // Create canvas
            int canvasWidth = WIDTH;
            int canvasHeight = HEIGHT - CONTROL_PANEL_HEIGHT;
            System.out.println("[UI] Creating canvas with dimensions: " + canvasWidth + "x" + canvasHeight);
            canvas = new Canvas(canvasWidth, canvasHeight);
            gc = canvas.getGraphicsContext2D();
            System.out.println("[UI] Graphics context obtained: " + (gc != null ? "SUCCESS" : "FAILED"));
            
            canvasContainer = new StackPane(canvas);
            System.out.println("[UI] Canvas container created");
            
            // Create control panel
            System.out.println("[UI] Creating control panel");
            createControlPanel();
            
            // Create settings panel
            System.out.println("[UI] Creating settings panel");
            createSettingsPanel();
            
            // Layout components
            System.out.println("[UI] Laying out components");
            mainLayout.setCenter(canvasContainer);
            mainLayout.setBottom(controlPanel);
            
            // Make canvas resizable with proper bounds
            System.out.println("[UI] Setting up canvas resize listeners");
            canvasContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    if (newVal.doubleValue() > 0) {
                        System.out.println("[UI] Canvas width changed to: " + newVal.doubleValue());
                        canvas.setWidth(newVal.doubleValue());
                    }
                } catch (Exception e) {
                    System.err.println("[ERROR] Error resizing canvas width: " + e.getMessage());
                }
            });
            canvasContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    if (newVal.doubleValue() > 0) {
                        System.out.println("[UI] Canvas height changed to: " + newVal.doubleValue());
                        canvas.setHeight(newVal.doubleValue());
                    }
                } catch (Exception e) {
                    System.err.println("[ERROR] Error resizing canvas height: " + e.getMessage());
                }
            });
            
            System.out.println("[UI] UI creation completed successfully");
        } catch (Exception e) {
            System.err.println("[ERROR] Fatal error creating UI: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to stop initialization
        }
    }
    
    /**
     * Creates the bottom control panel with audio controls.
     */
    private void createControlPanel() {
        controlPanel = new VBox(10);
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setStyle("-fx-background-color: #333333;");
        
        // Audio controls row
        HBox audioControls = new HBox(15);
        audioControls.setAlignment(Pos.CENTER);
        
        // Play/pause button
        playPauseButton = new Button("⏸️");
        playPauseButton.setOnAction(e -> togglePlayPause());
        
        // Volume control
        Label volumeLabel = new Label("Volume:");
        volumeLabel.setTextFill(Color.WHITE);
        volumeSlider = new Slider(0, 1, 0.7);
        volumeSlider.setPrefWidth(100);
        
        // Log slider creation and disable auto-adjusting
        System.out.println("[VOLUME] Volume slider created with initial value: " + volumeSlider.getValue());
        volumeSlider.setSnapToTicks(false);
        volumeSlider.setMajorTickUnit(0.1);
        volumeSlider.setBlockIncrement(0.05);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Prevent feedback loops and rate limit volume changes
            if (isUpdatingVolume) {
                System.out.println("[VOLUME] Ignoring volume change - update in progress");
                return;
            }
            
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastVolumeUpdateTime < VOLUME_UPDATE_THROTTLE_MS) {
                System.out.println("[VOLUME] Rate limiting volume change - too frequent");
                return;
            }
            
            lastVolumeUpdateTime = currentTime;
            
            // Check for suspicious rapid changes
            double oldValue = oldVal.doubleValue();
            double newValue = newVal.doubleValue();
            double changeDelta = Math.abs(newValue - oldValue);
            
            System.out.println("[VOLUME] Volume slider changed from " + oldValue + " to " + newValue + " (delta: " + changeDelta + ")");
            
            // Ignore very small changes that might be noise
            if (changeDelta < 0.01) {
                System.out.println("[VOLUME] Ignoring tiny volume change (< 0.01)");
                return;
            }
            
            // Set flag to prevent feedback
            isUpdatingVolume = true;
            
            try {
                // Use Platform.runLater to avoid potential threading issues
                Platform.runLater(() -> {
                    try {
                        if (mediaPlayer != null) {
                            // Check MediaPlayer state before changing volume
                            MediaPlayer.Status status = mediaPlayer.getStatus();
                            System.out.println("[VOLUME] MediaPlayer status: " + status);
                            
                            // Only set volume if MediaPlayer is in a valid state
                            if (status != MediaPlayer.Status.DISPOSED && 
                                status != MediaPlayer.Status.HALTED &&
                                status != MediaPlayer.Status.UNKNOWN) {
                                
                                double volume = Math.max(0.0, Math.min(1.0, newValue));
                                System.out.println("[VOLUME] Setting MediaPlayer volume to " + volume + " (clamped)");
                                
                                // Synchronize volume setting to prevent concurrent access
                                synchronized (mediaPlayer) {
                                    mediaPlayer.setVolume(volume);
                                }
                                System.out.println("[VOLUME] Volume set successfully");
                            } else {
                                System.out.println("[WARNING] Cannot set volume - MediaPlayer in invalid state: " + status);
                            }
                        } else {
                            System.out.println("[WARNING] Volume change attempted but MediaPlayer is null");
                        }
                    } catch (Exception e) {
                        System.err.println("[ERROR] Error setting volume: " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        // Always clear the update flag
                        isUpdatingVolume = false;
                    }
                });
            } catch (Exception e) {
                System.err.println("[ERROR] Error in volume change handler: " + e.getMessage());
                isUpdatingVolume = false;
            }
        });
        
        // Mode selector
        Label modeLabel = new Label("Mode:");
        modeLabel.setTextFill(Color.WHITE);
        modeSelector = new ComboBox<>();
        modeSelector.getItems().addAll("Spectrum Bars", "Particles", "Circular", "Waveform", "3D Spectrum", "Oscilloscope", "Mandala", "Star Field");
        modeSelector.setValue("Spectrum Bars");
        modeSelector.setOnAction(e -> {
            String selected = modeSelector.getValue();
            System.out.println("[UI] Visualization mode changed to: " + selected);
            try {
                switch (selected) {
                    case "Particles":
                        visualizationMode = MODE_PARTICLES;
                        System.out.println("[UI] Set to particle mode (" + MODE_PARTICLES + ")");
                        break;
                    case "Circular":
                        visualizationMode = MODE_CIRCULAR;
                        System.out.println("[UI] Set to circular mode (" + MODE_CIRCULAR + ")");
                        break;
                    case "Waveform":
                        visualizationMode = MODE_WAVEFORM;
                        System.out.println("[UI] Set to waveform mode (" + MODE_WAVEFORM + ")");
                        break;
                    case "3D Spectrum":
                        visualizationMode = MODE_3D_SPECTRUM;
                        System.out.println("[UI] Set to 3D spectrum mode (" + MODE_3D_SPECTRUM + ")");
                        break;
                    case "Oscilloscope":
                        visualizationMode = MODE_OSCILLOSCOPE;
                        System.out.println("[UI] Set to oscilloscope mode (" + MODE_OSCILLOSCOPE + ")");
                        break;
                    case "Mandala":
                        visualizationMode = MODE_MANDALA;
                        System.out.println("[UI] Set to mandala mode (" + MODE_MANDALA + ")");
                        break;
                    case "Star Field":
                        visualizationMode = MODE_STARFIELD;
                        System.out.println("[UI] Set to star field mode (" + MODE_STARFIELD + ")");
                        break;
                    default:
                        visualizationMode = MODE_SPECTRUM;
                        System.out.println("[UI] Set to spectrum mode (" + MODE_SPECTRUM + ")");
                        break;
                }
            } catch (Exception ex) {
                System.err.println("[ERROR] Error changing visualization mode: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        // Settings toggle
        Button settingsButton = new Button("⚙️");
        settingsButton.setOnAction(e -> toggleSettingsPanel());
        
        audioControls.getChildren().addAll(
            playPauseButton, volumeLabel, volumeSlider,
            modeLabel, modeSelector, settingsButton
        );
        
        // Progress bar
        seekBar = new ProgressBar(0);
        seekBar.setPrefWidth(400);
        
        controlPanel.getChildren().addAll(audioControls, seekBar);
    }
    
    /**
     * Creates the settings panel with customization sliders.
     */
    private void createSettingsPanel() {
        settingsPanel = new VBox(10);
        settingsPanel.setAlignment(Pos.TOP_CENTER);
        settingsPanel.setPadding(new Insets(10));
        settingsPanel.setStyle("-fx-background-color: #444444;");
        settingsPanel.setPrefWidth(SETTINGS_PANEL_WIDTH);
        
        Label title = new Label("Settings");
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Smoothing control
        Label smoothingLabel = new Label("Smoothing:");
        smoothingLabel.setTextFill(Color.WHITE);
        smoothingSlider = new Slider(0.1, 0.95, currentSmoothingFactor);
        smoothingSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            try {
                double newSmoothing = newVal.doubleValue();
                System.out.println("[UI] Smoothing factor changed from " + oldVal + " to " + newSmoothing);
                currentSmoothingFactor = newSmoothing;
            } catch (Exception e) {
                System.err.println("[ERROR] Error updating smoothing factor: " + e.getMessage());
            }
        });
        
        // Particle count control
        Label particleLabel = new Label("Particle Count:");
        particleLabel.setTextFill(Color.WHITE);
        particleCountSlider = new Slider(1, 50, currentParticleCount);
        particleCountSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int newCount = newVal.intValue();
                System.out.println("[UI] Particle count changed from " + oldVal + " to " + newCount);
                currentParticleCount = newCount;
            } catch (Exception e) {
                System.err.println("[ERROR] Error updating particle count: " + e.getMessage());
            }
        });
        
        settingsPanel.getChildren().addAll(
            title, smoothingLabel, smoothingSlider,
            particleLabel, particleCountSlider
        );
        
        settingsPanel.setVisible(false);
    }
    
    /**
     * Renders circular spectrum visualization.
     * 
     * @param gc Graphics context for rendering
     * @param now Current timestamp for animation
     */
    private void drawCircularSpectrum(GraphicsContext gc, long now) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double radius = Math.min(width, height) / 4.0;
        double colorShift = (now / 5_000_000_000.0) * 360;
        
        // Beat detection enhancement
        double beatMultiplier = beatDetected ? 1.0 + beatFlashIntensity : 1.0;
        
        for (int i = 0; i < OUTPUT_BANDS; i++) {
            if (smoothedMagnitudes[i] <= DB_THRESHOLD) continue;
            
            double angle = (i / (double) OUTPUT_BANDS) * 2 * Math.PI;
            double magnitude = (60 + smoothedMagnitudes[i]) * 2.0 * beatMultiplier;
            magnitude = Math.max(5, magnitude);
            
            double innerRadius = radius;
            double outerRadius = radius + magnitude;
            
            double x1 = centerX + Math.cos(angle) * innerRadius;
            double y1 = centerY + Math.sin(angle) * innerRadius;
            double x2 = centerX + Math.cos(angle) * outerRadius;
            double y2 = centerY + Math.sin(angle) * outerRadius;
            
            double dynamicHue = (i * 5 + colorShift) % 360;
            double brightness = Math.min(1.0, 0.4 + (smoothedMagnitudes[i] + 60) / 120.0);
            double saturation = Math.min(1.0, 0.8 + (smoothedMagnitudes[i] + 60) / 150.0);
            
            gc.setStroke(Color.hsb(dynamicHue, saturation, brightness));
            gc.setLineWidth(3);
            gc.strokeLine(x1, y1, x2, y2);
        }
    }
    
    /**
     * Updates beat detection based on current audio energy.
     */
    private void updateBeatDetection() {
        // Calculate current energy
        double currentEnergy = 0;
        for (float magnitude : magnitudes) {
            currentEnergy += Math.abs(magnitude);
        }
        
        // Add to history
        energyHistory[energyHistoryIndex] = currentEnergy;
        energyHistoryIndex = (energyHistoryIndex + 1) % BEAT_HISTORY_SIZE;
        
        // Calculate average energy
        double averageEnergy = 0;
        for (double energy : energyHistory) {
            averageEnergy += energy;
        }
        averageEnergy /= BEAT_HISTORY_SIZE;
        
        // Detect beat
        beatDetected = currentEnergy > averageEnergy * BEAT_SENSITIVITY;
        
        // Update flash intensity
        if (beatDetected) {
            beatFlashIntensity = 1.0;
        } else {
            beatFlashIntensity *= 0.95; // Decay flash
        }
    }
    
    /**
     * Toggles play/pause state of the media player.
     */
    private void togglePlayPause() {
        System.out.println("[PLAYBACK] Toggle play/pause requested");
        if (mediaPlayer != null) {
            try {
                MediaPlayer.Status status = mediaPlayer.getStatus();
                System.out.println("[PLAYBACK] Current MediaPlayer status: " + status);
                
                if (status == MediaPlayer.Status.PLAYING) {
                    System.out.println("[PLAYBACK] Pausing playback");
                    mediaPlayer.pause();
                    playPauseButton.setText("▶️");
                    System.out.println("[PLAYBACK] Playback paused successfully");
                } else {
                    System.out.println("[PLAYBACK] Starting playback");
                    mediaPlayer.play();
                    playPauseButton.setText("⏸️");
                    System.out.println("[PLAYBACK] Playback started successfully");
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Error during play/pause toggle: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("[ERROR] Play/pause attempted but MediaPlayer is null");
        }
    }
    
    /**
     * Toggles fullscreen mode.
     * 
     * @param stage Primary stage for fullscreen control
     */
    private void toggleFullscreen(Stage stage) {
        System.out.println("[UI] Toggling fullscreen mode from " + isFullscreen + " to " + !isFullscreen);
        try {
            isFullscreen = !isFullscreen;
            stage.setFullScreen(isFullscreen);
            
            if (isFullscreen) {
                System.out.println("[UI] Entering fullscreen - hiding controls");
                // Hide controls in fullscreen
                controlPanel.setVisible(false);
                settingsPanel.setVisible(false);
            } else {
                System.out.println("[UI] Exiting fullscreen - showing controls");
                // Show controls when exiting fullscreen
                controlPanel.setVisible(true);
            }
            System.out.println("[UI] Fullscreen toggle completed");
        } catch (Exception e) {
            System.err.println("[ERROR] Error toggling fullscreen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Toggles settings panel visibility.
     */
    private void toggleSettingsPanel() {
        System.out.println("[UI] Toggling settings panel from " + settingsVisible + " to " + !settingsVisible);
        try {
            settingsVisible = !settingsVisible;
            settingsPanel.setVisible(settingsVisible);
            
            if (settingsVisible) {
                System.out.println("[UI] Showing settings panel");
                mainLayout.setRight(settingsPanel);
            } else {
                System.out.println("[UI] Hiding settings panel");
                mainLayout.setRight(null);
            }
            System.out.println("[UI] Settings panel toggle completed");
        } catch (Exception e) {
            System.err.println("[ERROR] Error toggling settings panel: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Safely updates the volume slider value without triggering the change listener.
     * Used to sync UI with MediaPlayer state without causing feedback loops.
     * 
     * @param volume New volume value (0.0 to 1.0)
     */
    private void updateVolumeSliderSafely(double volume) {
        if (volumeSlider == null) return;
        
        System.out.println("[VOLUME] Safely updating volume slider to: " + volume);
        isUpdatingVolume = true;
        try {
            Platform.runLater(() -> {
                try {
                    volumeSlider.setValue(Math.max(0.0, Math.min(1.0, volume)));
                    System.out.println("[VOLUME] Volume slider updated safely");
                } catch (Exception e) {
                    System.err.println("[ERROR] Error updating volume slider: " + e.getMessage());
                } finally {
                    isUpdatingVolume = false;
                }
            });
        } catch (Exception e) {
            System.err.println("[ERROR] Error in safe volume slider update: " + e.getMessage());
            isUpdatingVolume = false;
        }
    }

    /**
     * Displays a file chooser dialog for audio file selection.
     * Supports MP3, WAV, and M4A formats.
     * 
     * @param stage Parent stage for the file dialog
     * @return Selected audio file, or null if no file selected
     */
    private File selectAudioFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Audio File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.m4a"),
            new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            showInfoDialog("No File Selected", "Application will exit.");
            return null;
        }
        
        if (!file.exists()) {
            showErrorDialog("File Not Found", "Selected file does not exist: " + file.getPath());
            return null;
        }
        
        return file;
    }

    /**
     * Initializes the MediaPlayer with the selected audio file.
     * Sets up error handling and media loading.
     * 
     * @param file Audio file to load
     * @return true if initialization successful, false otherwise
     */
    private boolean initializeMediaPlayer(File file) {
        try {
            System.out.println("Initializing media player with file: " + file.getAbsolutePath());
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            
            // Set up error handling for media player
            mediaPlayer.setOnError(() -> {
                handleMediaError("Media playback error", mediaPlayer.getError());
            });
            
            System.out.println("Media player initialized successfully");
            return true;
            
        } catch (MediaException e) {
            handleMediaError("Failed to load media file", e);
            return false;
        } catch (Exception e) {
            handleMediaError("Unexpected error loading media", e);
            return false;
        }
    }

    /**
     * Configures audio spectrum analysis parameters and listener.
     * Sets up the callback for receiving real-time audio data.
     */
    private void setupAudioProcessing() {
        System.out.println("[AUDIO] Setting up audio spectrum processing");
        try {
            System.out.println("[AUDIO] Setting spectrum bands to: " + INPUT_BANDS);
            mediaPlayer.setAudioSpectrumNumBands(INPUT_BANDS);
            
            System.out.println("[AUDIO] Setting spectrum interval to: " + SPECTRUM_INTERVAL_SECONDS + " seconds");
            mediaPlayer.setAudioSpectrumInterval(SPECTRUM_INTERVAL_SECONDS);
            
            System.out.println("[AUDIO] Setting spectrum listener");
            mediaPlayer.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {
                try {
                    if (magnitudes != null && magnitudes.length == INPUT_BANDS) {
                        // Monitor for potential crashes in audio processing
                        long currentTimeMs = System.currentTimeMillis();
                        
                        if (currentTimeMs - lastAudioLogTime > 10000) { // Log every 10 seconds
                            float maxMagnitude = Float.NEGATIVE_INFINITY;
                            float minMagnitude = Float.POSITIVE_INFINITY;
                            for (float mag : magnitudes) {
                                maxMagnitude = Math.max(maxMagnitude, mag);
                                minMagnitude = Math.min(minMagnitude, mag);
                            }
                            System.out.println("[AUDIO] Spectrum data health: timestamp=" + timestamp + ", duration=" + duration + ", magnitude range=[" + minMagnitude + " to " + maxMagnitude + "]");
                            lastAudioLogTime = currentTimeMs;
                        }
                        
                        System.arraycopy(magnitudes, 0, this.magnitudes, 0, INPUT_BANDS);
                        processWaveform();
                        spawnParticles();
                        
                        // Update seek bar safely
                        Platform.runLater(() -> {
                            try {
                                if (mediaPlayer != null && seekBar != null && 
                                    mediaPlayer.getTotalDuration() != null && 
                                    !mediaPlayer.getTotalDuration().isUnknown()) {
                                    double progress = mediaPlayer.getCurrentTime().toMillis() / 
                                                    mediaPlayer.getTotalDuration().toMillis();
                                    seekBar.setProgress(Math.max(0, Math.min(1, progress)));
                                }
                            } catch (Exception e) {
                                System.err.println("[ERROR] Error updating seek bar: " + e.getMessage());
                            }
                        });
                    } else {
                        if (magnitudes == null) {
                            System.err.println("[WARNING] Received null magnitudes in spectrum listener");
                        } else {
                            System.err.println("[WARNING] Unexpected magnitude array length: " + magnitudes.length + ", expected: " + INPUT_BANDS);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[ERROR] Error in audio spectrum processing: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            System.out.println("[AUDIO] Audio processing setup completed successfully");
        } catch (Exception e) {
            System.err.println("[ERROR] Error setting up audio processing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates and configures the animation timer for rendering.
     */
    private void setupAnimationTimer() {
        System.out.println("[ANIMATION] Setting up animation timer");
        animationTimer = new AnimationTimer() {
            private long lastLogTime = 0;
            private int frameCount = 0;
            private long startTime = System.nanoTime();
            
            @Override
            public void handle(long now) {
                frameCount++;
                try {
                    interpolateAndFilterMagnitudes();
                    updateParticles();
                    drawVisualizer(gc, now);
                    
                    // Log every 5 seconds
                    if (now - lastLogTime > 5_000_000_000L) {
                        long elapsedSeconds = (now - startTime) / 1_000_000_000L;
                        System.out.println("[ANIMATION] Rendering at " + (frameCount / 5) + " FPS (" + frameCount + " frames in 5s) - Total runtime: " + elapsedSeconds + "s");
                        lastLogTime = now;
                        frameCount = 0;
                        
                        // Extended monitoring in crash zone (20-35 seconds)
                        if (elapsedSeconds >= 15) {
                            System.out.println("[ANIMATION] CRASH ZONE: Runtime " + elapsedSeconds + "s - Deep monitoring active");
                            
                            // Check MediaPlayer health
                            if (mediaPlayer != null) {
                                try {
                                    MediaPlayer.Status status = mediaPlayer.getStatus();
                                    Duration currentTime = mediaPlayer.getCurrentTime();
                                    Duration totalDuration = mediaPlayer.getTotalDuration();
                                    double volume = mediaPlayer.getVolume();
                                    System.out.println("[ANIMATION] MediaPlayer health at " + elapsedSeconds + "s: status=" + status + ", time=" + currentTime + ", duration=" + totalDuration + ", volume=" + volume);
                                } catch (Exception e) {
                                    System.err.println("[ERROR] MediaPlayer check failed at " + elapsedSeconds + "s: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            } else {
                                System.err.println("[ERROR] MediaPlayer is null at " + elapsedSeconds + "s");
                            }
                            
                            // Check UI components health
                            if (canvas != null && gc != null) {
                                double width = canvas.getWidth();
                                double height = canvas.getHeight();
                                System.out.println("[ANIMATION] Canvas/GC health at " + elapsedSeconds + "s: canvas=" + width + "x" + height + ", gc=" + gc.getClass().getSimpleName());
                            } else {
                                System.err.println("[ERROR] Canvas or GC is null at " + elapsedSeconds + "s");
                            }
                            
                            // Check audio data arrays
                            try {
                                int magCount = (magnitudes != null) ? magnitudes.length : 0;
                                int smoothCount = (smoothedMagnitudes != null) ? smoothedMagnitudes.length : 0;
                                int waveCount = (waveformPoints != null) ? waveformPoints.length : 0;
                                System.out.println("[ANIMATION] Audio arrays at " + elapsedSeconds + "s: mag=" + magCount + ", smooth=" + smoothCount + ", wave=" + waveCount);
                            } catch (Exception e) {
                                System.err.println("[ERROR] Audio array check failed at " + elapsedSeconds + "s: " + e.getMessage());
                            }
                            
                            // Check particle system
                            try {
                                int particleCount = (particles != null) ? particles.size() : -1;
                                System.out.println("[ANIMATION] Particle system at " + elapsedSeconds + "s: count=" + particleCount + ", mode=" + visualizationMode);
                            } catch (Exception e) {
                                System.err.println("[ERROR] Particle system check failed at " + elapsedSeconds + "s: " + e.getMessage());
                            }
                            
                            // Memory usage check
                            try {
                                Runtime runtime = Runtime.getRuntime();
                                long totalMemory = runtime.totalMemory();
                                long freeMemory = runtime.freeMemory();
                                long usedMemory = totalMemory - freeMemory;
                                long maxMemory = runtime.maxMemory();
                                System.out.println("[ANIMATION] Memory at " + elapsedSeconds + "s: used=" + (usedMemory/1024/1024) + "MB, total=" + (totalMemory/1024/1024) + "MB, max=" + (maxMemory/1024/1024) + "MB");
                            } catch (Exception e) {
                                System.err.println("[ERROR] Memory check failed at " + elapsedSeconds + "s: " + e.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[ERROR] Rendering error: " + e.getMessage());
                    e.printStackTrace();
                    // Don't stop the timer on error, just log and continue
                }
            }
        };
        System.out.println("[ANIMATION] Animation timer setup completed");
    }

    /**
     * Sets up MediaPlayer event handlers for playback control.
     * Configures auto-play and looping behavior.
     */
    private void setupMediaPlayerCallbacks() {
        System.out.println("[MEDIA] Setting up MediaPlayer callbacks");
        
        mediaPlayer.setOnReady(() -> {
            System.out.println("[MEDIA] MediaPlayer ready event triggered");
            try {
                // Set initial volume
                double initialVolume = volumeSlider.getValue();
                System.out.println("[MEDIA] Setting initial volume to: " + initialVolume);
                synchronized (mediaPlayer) {
                    mediaPlayer.setVolume(initialVolume);
                }
                
                // Start playback
                System.out.println("[MEDIA] Starting media playback");
                mediaPlayer.play();
                
                // Start animation timer
                if (animationTimer != null) {
                    System.out.println("[MEDIA] Starting animation timer");
                    animationTimer.start();
                } else {
                    System.err.println("[ERROR] Animation timer is null!");
                }
                
                // Update UI
                Platform.runLater(() -> {
                    System.out.println("[UI] Updating play button to pause state");
                    playPauseButton.setText("⏸️");
                });
                
                System.out.println("[MEDIA] MediaPlayer ready setup completed");
            } catch (Exception e) {
                System.err.println("[ERROR] Error in MediaPlayer ready callback: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        mediaPlayer.setOnEndOfMedia(() -> {
            System.out.println("[MEDIA] End of media reached, looping");
            try {
                mediaPlayer.seek(mediaPlayer.getStartTime());
                mediaPlayer.play();
                System.out.println("[MEDIA] Media loop successful");
            } catch (Exception e) {
                System.err.println("[ERROR] Error during media loop: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        mediaPlayer.setOnPlaying(() -> {
            System.out.println("[MEDIA] MediaPlayer playing state");
        });
        
        mediaPlayer.setOnPaused(() -> {
            System.out.println("[MEDIA] MediaPlayer paused state");
        });
        
        mediaPlayer.setOnStopped(() -> {
            System.out.println("[MEDIA] MediaPlayer stopped state");
        });
        
        // Add error handling
        mediaPlayer.setOnError(() -> {
            System.err.println("[ERROR] MediaPlayer error event triggered");
            try {
                Exception error = mediaPlayer.getError();
                if (error != null) {
                    System.err.println("[ERROR] MediaPlayer error details: " + error.getMessage());
                    error.printStackTrace();
                    handleMediaError("Playback error", error);
                } else {
                    System.err.println("[ERROR] MediaPlayer error with no exception details");
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Error in error handler: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        System.out.println("[MEDIA] MediaPlayer callbacks setup completed");
    }

    /**
     * Configures keyboard event handling for the scene.
     * 
     * @param scene Scene to attach key handlers to
     * @param stage Primary stage for fullscreen control
     */
    private void setupKeyHandling(Scene scene, Stage stage) {
        System.out.println("[INPUT] Setting up keyboard handling");
        scene.setOnKeyPressed(event -> {
            System.out.println("[INPUT] Key pressed: " + event.getCode());
            try {
                switch (event.getCode()) {
                    case DIGIT1:
                        System.out.println("[INPUT] Switching to spectrum mode via key 1");
                        visualizationMode = MODE_SPECTRUM;
                        modeSelector.setValue("Spectrum Bars");
                        break;
                    case DIGIT2:
                        System.out.println("[INPUT] Switching to particle mode via key 2");
                        visualizationMode = MODE_PARTICLES;
                        modeSelector.setValue("Particles");
                        break;
                    case DIGIT3:
                        System.out.println("[INPUT] Switching to circular mode via key 3");
                        visualizationMode = MODE_CIRCULAR;
                        modeSelector.setValue("Circular");
                        break;
                    case DIGIT4:
                        System.out.println("[INPUT] Switching to waveform mode via key 4");
                        visualizationMode = MODE_WAVEFORM;
                        modeSelector.setValue("Waveform");
                        break;
                    case DIGIT5:
                        System.out.println("[INPUT] Switching to 3D spectrum mode via key 5");
                        visualizationMode = MODE_3D_SPECTRUM;
                        modeSelector.setValue("3D Spectrum");
                        break;
                    case DIGIT6:
                        System.out.println("[INPUT] Switching to oscilloscope mode via key 6");
                        visualizationMode = MODE_OSCILLOSCOPE;
                        modeSelector.setValue("Oscilloscope");
                        break;
                    case DIGIT7:
                        System.out.println("[INPUT] Switching to mandala mode via key 7");
                        visualizationMode = MODE_MANDALA;
                        modeSelector.setValue("Mandala");
                        break;
                    case DIGIT8:
                        System.out.println("[INPUT] Switching to star field mode via key 8");
                        visualizationMode = MODE_STARFIELD;
                        modeSelector.setValue("Star Field");
                        break;
                    case SPACE:
                        System.out.println("[INPUT] Space key pressed - toggling play/pause");
                        togglePlayPause();
                        break;
                    case F11:
                        System.out.println("[INPUT] F11 pressed - toggling fullscreen");
                        toggleFullscreen(stage);
                        break;
                    case S:
                        if (event.isControlDown()) {
                            System.out.println("[INPUT] Ctrl+S pressed - toggling settings panel");
                            toggleSettingsPanel();
                        }
                        break;
                    case ESCAPE:
                        System.out.println("[INPUT] Escape pressed");
                        if (isFullscreen) {
                            System.out.println("[INPUT] Exiting fullscreen mode");
                            toggleFullscreen(stage);
                        } else {
                            System.out.println("[INPUT] Exiting application");
                            cleanup();
                            Platform.exit();
                        }
                        break;
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Error handling key press: " + e.getMessage());
                e.printStackTrace();
            }
        });
        System.out.println("[INPUT] Keyboard handling setup completed");
    }

    /**
     * Spawns new particles based on audio intensity.
     * Only spawns particles when in particle mode and audio exceeds threshold.
     */
    private void spawnParticles() {
        if (visualizationMode != MODE_PARTICLES) return;
        
        for (int i = 0; i < currentParticleCount; i++) {
            double strength = Math.abs(magnitudes[0]); 
            if (strength > PARTICLE_SPAWN_INTENSITY_THRESHOLD) { 
                particles.add(new Particle(canvas.getWidth() / 2.0, canvas.getHeight() / 2.0, strength * 4));
            }
        }
    }

    /**
     * Processes smoothed magnitude data into waveform points for overlay rendering.
     * Maps frequency bands to screen coordinates using interpolation.
     */
    private void processWaveform() {
        if (smoothedMagnitudes == null || smoothedMagnitudes.length == 0 || canvas == null) return;
        
        double canvasHeight = canvas.getHeight();
        if (canvasHeight <= 0) canvasHeight = HEIGHT - CONTROL_PANEL_HEIGHT;
        
        for (int i = 0; i < OUTPUT_BANDS; i++) {
            double mappedIndex = i * (INPUT_BANDS - 1.0) / (OUTPUT_BANDS - 1.0);
            int lowerIndex = (int) Math.floor(mappedIndex);
            int upperIndex = Math.min(lowerIndex + 1, INPUT_BANDS - 1);
            double fraction = mappedIndex - lowerIndex;

            if (lowerIndex < 0 || upperIndex >= smoothedMagnitudes.length) continue;

            float interpolatedValue = (float) ((1 - fraction) * smoothedMagnitudes[lowerIndex] + 
                                             fraction * smoothedMagnitudes[upperIndex]);

            waveformPoints[i] = (float) (canvasHeight / 2.0 - interpolatedValue * 2.5);
        }
    }

    /**
     * Interpolates input frequency bands to output resolution and applies smoothing.
     * Filters out bands below threshold and handles empty data gracefully.
     */
    private void interpolateAndFilterMagnitudes() {
        List<Float> activeBands = new ArrayList<>();
        for (int i = 0; i < INPUT_BANDS; i++) {
            if (magnitudes[i] > DB_THRESHOLD) {
                activeBands.add(magnitudes[i]);
            }
        }

        if (activeBands.isEmpty()) {
            for (int i = 0; i < INPUT_BANDS; i++) {
                activeBands.add(magnitudes[i] + 10);
            }
        }

        int activeCount = activeBands.size();

        for (int i = 0; i < OUTPUT_BANDS; i++) {
            double mappedIndex = i * (activeCount - 1.0) / (OUTPUT_BANDS - 1.0);
            int lowerIndex = (int) Math.floor(mappedIndex);
            int upperIndex = Math.min(lowerIndex + 1, activeCount - 1);
            double fraction = mappedIndex - lowerIndex;

            float interpolatedValue = (float) ((1 - fraction) * activeBands.get(lowerIndex) + 
                                             fraction * activeBands.get(upperIndex));

            smoothedMagnitudes[i] = (float) (currentSmoothingFactor * smoothedMagnitudes[i] + 
                                           (1 - currentSmoothingFactor) * interpolatedValue);
        }
    }

    /**
     * Updates all active particles and removes expired ones.
     * Only processes particles when in particle visualization mode.
     */
    private void updateParticles() {
        if (visualizationMode != MODE_PARTICLES) return;

        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            p.update();
            if (p.lifetime <= 0) {
                iterator.remove();
            }
        }
    }

    /**
     * Main rendering method that draws the appropriate visualization mode.
     * 
     * @param gc Graphics context for rendering
     * @param now Current timestamp for animation timing
     */
    private void drawVisualizer(GraphicsContext gc, long now) {
        if (gc == null) {
            System.err.println("[ERROR] Graphics context is null in drawVisualizer");
            return;
        }
        if (canvas == null) {
            System.err.println("[ERROR] Canvas is null in drawVisualizer");
            return;
        }
        
        try {
            double width = canvas.getWidth();
            double height = canvas.getHeight();
            
            // Use default dimensions if canvas not properly sized yet
            if (width <= 0) {
                width = WIDTH;
                System.out.println("[RENDER] Using default width: " + width);
            }
            if (height <= 0) {
                height = HEIGHT - CONTROL_PANEL_HEIGHT;
                System.out.println("[RENDER] Using default height: " + height);
            }
            
            // Clear canvas
            gc.clearRect(0, 0, width, height);
            gc.setFill(beatDetected ? Color.gray(0.1 + beatFlashIntensity * 0.2) : Color.BLACK);
            gc.fillRect(0, 0, width, height);
            
            // Update beat detection
            updateBeatDetection();
            
            switch (visualizationMode) {
                case MODE_PARTICLES:
                    drawParticles(gc);
                    break;
                case MODE_CIRCULAR:
                    drawCircularSpectrum(gc, now);
                    break;
                case MODE_WAVEFORM:
                    drawWaveformMode(gc, now);
                    break;
                case MODE_3D_SPECTRUM:
                    draw3DSpectrum(gc, now);
                    break;
                case MODE_OSCILLOSCOPE:
                    drawOscilloscope(gc, now);
                    break;
                case MODE_MANDALA:
                    drawMandala(gc, now);
                    break;
                case MODE_STARFIELD:
                    drawStarField(gc, now);
                    break;
                default:
                    drawSpectrum(gc, now);
                    drawWaveform(gc);
                    break;
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Exception in drawVisualizer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Renders all active particles with HSB color based on lifetime.
     * 
     * @param gc Graphics context for rendering
     */
    private void drawParticles(GraphicsContext gc) {
        for (Particle p : particles) {
            gc.setFill(Color.hsb(p.hue, 1.0, p.lifetime / PARTICLE_LIFETIME_FRAMES));
            gc.fillOval(p.x, p.y, p.size, p.size);
        }
    }

    /**
     * Renders spectrum bars with dynamic colors and frequency boosting.
     * 
     * @param gc Graphics context for rendering
     * @param now Current timestamp for color animation
     */
    private void drawSpectrum(GraphicsContext gc, long now) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double bandWidth = width / (double) OUTPUT_BANDS;
        double colorShift = (now / 5_000_000_000.0) * 360;
        
        // Beat detection enhancement
        double beatMultiplier = beatDetected ? 1.0 + beatFlashIntensity : 1.0;

        for (int i = 0; i < OUTPUT_BANDS; i++) {
            if (smoothedMagnitudes[i] <= DB_THRESHOLD) continue;

            double x = i * bandWidth;
            double barHeight = (60 + smoothedMagnitudes[i]) * 3.5 * beatMultiplier;
            barHeight = Math.max(MIN_HEIGHT, barHeight);
            double frequencyBoost = 1 + (i / (double) OUTPUT_BANDS) * 2;
            barHeight *= frequencyBoost;

            double y = height / 2.0 - barHeight / 2.0;
            double dynamicHue = (i * 3 + colorShift) % 360;
            double brightness = Math.min(1.0, 0.4 + (smoothedMagnitudes[i] + 60) / 120.0);
            double saturation = Math.min(1.0, 0.8 + (smoothedMagnitudes[i] + 60) / 150.0);

            gc.setFill(Color.hsb(dynamicHue, saturation, brightness));
            gc.fillRect(x, y, bandWidth - 1, barHeight);
        }
    }

    /**
     * Renders waveform overlay as a white line across the spectrum.
     * 
     * @param gc Graphics context for rendering
     */
    private void drawWaveform(GraphicsContext gc) {
        double width = canvas.getWidth();
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.beginPath();
        gc.moveTo(0, waveformPoints[0]);

        for (int i = 1; i < OUTPUT_BANDS; i++) {
            double x = i * (width / (double) OUTPUT_BANDS);
            gc.lineTo(x, waveformPoints[i]);
        }
        gc.stroke();
    }
    
    /**
     * Renders waveform mode with scrolling history display.
     * 
     * @param gc Graphics context for rendering
     * @param now Current timestamp for animation
     */
    private void drawWaveformMode(GraphicsContext gc, long now) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        
        // Update waveform history
        updateWaveformHistory();
        
        // Draw background grid
        gc.setStroke(Color.gray(0.2));
        gc.setLineWidth(1);
        for (int i = 0; i < 10; i++) {
            double y = (i / 9.0) * height;
            gc.strokeLine(0, y, width, y);
        }
        for (int i = 0; i < 20; i++) {
            double x = (i / 19.0) * width;
            gc.strokeLine(x, 0, x, height);
        }
        
        // Draw waveform history with color gradient
        for (int historyIndex = 0; historyIndex < waveformHistory.length; historyIndex++) {
            float[] waveData = waveformHistory[(waveformHistoryIndex - historyIndex + waveformHistory.length) % waveformHistory.length];
            
            // Calculate color based on age (newer = brighter)
            double age = historyIndex / (double) waveformHistory.length;
            double alpha = 1.0 - age;
            double hue = (now / 10_000_000_000.0 * 360) % 360;
            
            gc.setStroke(Color.hsb(hue, 0.8, alpha * 0.8));
            gc.setLineWidth(2 - age);
            
            gc.beginPath();
            boolean firstPoint = true;
            
            for (int i = 0; i < OUTPUT_BANDS; i++) {
                double x = (i / (double) OUTPUT_BANDS) * width;
                // Convert magnitude to visual height (magnitudes are in dB, typically -60 to 0)
                double normalizedMag = Math.max(0, (waveData[i] + 60) / 60.0); // Normalize -60dB to 0dB -> 0 to 1
                double y = height / 2.0 + (normalizedMag - 0.5) * height * 0.8; // Center around middle with good range
                
                if (firstPoint) {
                    gc.moveTo(x, y);
                    firstPoint = false;
                } else {
                    gc.lineTo(x, y);
                }
            }
            gc.stroke();
        }
    }
    
    /**
     * Renders 3D spectrum with perspective effects.
     * 
     * @param gc Graphics context for rendering
     * @param now Current timestamp for animation
     */
    private void draw3DSpectrum(GraphicsContext gc, long now) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double colorShift = (now / 5_000_000_000.0) * 360;
        double beatMultiplier = beatDetected ? 1.0 + beatFlashIntensity : 1.0;
        
        // 3D perspective parameters
        double vanishingPointX = width / 2.0;
        double vanishingPointY = height * 0.3;
        double baseY = height * 0.8;
        double baseWidth = width * 0.8;
        
        for (int i = 0; i < OUTPUT_BANDS; i++) {
            if (smoothedMagnitudes[i] <= DB_THRESHOLD) continue;
            
            // Calculate 3D position
            double t = i / (double) (OUTPUT_BANDS - 1);
            double perspectiveFactor = PERSPECTIVE_FACTOR + (1 - PERSPECTIVE_FACTOR) * Math.pow(t, 2);
            
            double barHeight = (60 + smoothedMagnitudes[i]) * 4.0 * beatMultiplier;
            barHeight = Math.max(MIN_HEIGHT, barHeight);
            
            // Front and back positions
            double frontX = vanishingPointX + (t - 0.5) * baseWidth * perspectiveFactor;
            double backX = vanishingPointX + (t - 0.5) * baseWidth * 0.3;
            double frontY = baseY;
            double backY = vanishingPointY;
            
            // Bar top positions
            double frontTopY = frontY - barHeight * perspectiveFactor;
            double backTopY = backY - barHeight * 0.3;
            
            // Draw 3D bar as a trapezoid
            double dynamicHue = (i * 3 + colorShift) % 360;
            double brightness = Math.min(1.0, 0.4 + (smoothedMagnitudes[i] + 60) / 120.0);
            double saturation = Math.min(1.0, 0.8 + (smoothedMagnitudes[i] + 60) / 150.0);
            
            // Main face
            gc.setFill(Color.hsb(dynamicHue, saturation, brightness));
            gc.fillPolygon(
                new double[]{frontX - 2, frontX + 2, backX + 1, backX - 1},
                new double[]{frontY, frontY, backY, backY},
                4
            );
            
            // Top face
            gc.setFill(Color.hsb(dynamicHue, saturation, brightness * 1.2));
            gc.fillPolygon(
                new double[]{frontX - 2, frontX + 2, backX + 1, backX - 1},
                new double[]{frontTopY, frontTopY, backTopY, backTopY},
                4
            );
            
            // Side face
            gc.setFill(Color.hsb(dynamicHue, saturation, brightness * 0.7));
            gc.fillPolygon(
                new double[]{frontX + 2, backX + 1, backX + 1, frontX + 2},
                new double[]{frontY, backY, backTopY, frontTopY},
                4
            );
        }
    }
    
    /**
     * Renders oscilloscope mode with circular audio patterns.
     * 
     * @param gc Graphics context for rendering
     * @param now Current timestamp for animation
     */
    private void drawOscilloscope(GraphicsContext gc, long now) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double radius = Math.min(width, height) * 0.3;
        
        // Get current audio sample (use first magnitude as sample)
        float currentSample = magnitudes.length > 0 ? magnitudes[0] : 0;
        
        // Calculate scope position
        double angle = (now / 1_000_000_000.0) * Math.PI * 2 * 0.5; // Slow rotation
        double sampleRadius = radius + currentSample * 2;
        double x = centerX + Math.cos(angle) * sampleRadius;
        double y = centerY + Math.sin(angle) * sampleRadius;
        
        // Add to trail
        scopeTrail.add(new Point2D(x, y));
        
        // Limit trail length
        while (scopeTrail.size() > 300) {
            scopeTrail.remove(0);
        }
        
        // Draw oscilloscope grid
        gc.setStroke(Color.gray(0.3));
        gc.setLineWidth(1);
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        gc.strokeLine(centerX - radius, centerY, centerX + radius, centerY);
        gc.strokeLine(centerX, centerY - radius, centerX, centerY + radius);
        
        // Draw trail
        if (scopeTrail.size() > 1) {
            gc.setLineWidth(2);
            for (int i = 1; i < scopeTrail.size(); i++) {
                Point2D p1 = scopeTrail.get(i - 1);
                Point2D p2 = scopeTrail.get(i);
                
                double age = i / (double) scopeTrail.size();
                double hue = (now / 10_000_000_000.0 * 360) % 360;
                gc.setStroke(Color.hsb(hue, 1.0, age));
                gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            }
        }
        
        // Draw current point
        gc.setFill(Color.WHITE);
        gc.fillOval(x - 3, y - 3, 6, 6);
        
        previousSample = currentSample;
    }
    
    /**
     * Renders mandala/kaleidoscope with symmetric patterns.
     * 
     * @param gc Graphics context for rendering
     * @param now Current timestamp for animation
     */
    private void drawMandala(GraphicsContext gc, long now) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double maxRadius = Math.min(width, height) / 2.0;
        
        int symmetryOrder = 8; // 8-fold symmetry
        double rotationSpeed = (now / 20_000_000_000.0) * 360; // Slow rotation
        
        gc.save();
        gc.translate(centerX, centerY);
        
        for (int sym = 0; sym < symmetryOrder; sym++) {
            gc.save();
            gc.rotate((360.0 / symmetryOrder) * sym + rotationSpeed);
            
            // Draw one segment that will be repeated
            for (int i = 0; i < OUTPUT_BANDS / 4; i++) { // Use subset for performance
                if (smoothedMagnitudes[i] <= DB_THRESHOLD) continue;
                
                double angle = (i / (double) (OUTPUT_BANDS / 4)) * 45; // 45 degree segment
                double magnitude = (60 + smoothedMagnitudes[i]) * 2.0;
                magnitude = Math.max(5, magnitude);
                
                double startRadius = maxRadius * 0.2;
                double endRadius = startRadius + magnitude;
                
                double x1 = Math.cos(Math.toRadians(angle)) * startRadius;
                double y1 = Math.sin(Math.toRadians(angle)) * startRadius;
                double x2 = Math.cos(Math.toRadians(angle)) * endRadius;
                double y2 = Math.sin(Math.toRadians(angle)) * endRadius;
                
                double hue = (angle * 2 + rotationSpeed) % 360;
                double brightness = Math.min(1.0, 0.4 + (smoothedMagnitudes[i] + 60) / 120.0);
                
                gc.setStroke(Color.hsb(hue, 1.0, brightness));
                gc.setLineWidth(3);
                gc.strokeLine(x1, y1, x2, y2);
                
                // Draw connecting arcs
                if (i > 0) {
                    double prevAngle = ((i - 1) / (double) (OUTPUT_BANDS / 4)) * 45;
                    double prevEndRadius = startRadius + Math.max(5, (60 + smoothedMagnitudes[i - 1]) * 2.0);
                    
                    gc.setLineWidth(1);
                    gc.strokeArc(
                        -prevEndRadius, -prevEndRadius, 
                        prevEndRadius * 2, prevEndRadius * 2,
                        prevAngle, angle - prevAngle,
                        javafx.scene.shape.ArcType.OPEN
                    );
                }
            }
            
            gc.restore();
        }
        
        gc.restore();
    }
    
    /**
     * Renders star field with particles responding to bass frequencies.
     * 
     * @param gc Graphics context for rendering
     * @param now Current timestamp for animation
     */
    private void drawStarField(GraphicsContext gc, long now) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        
        // Update stars based on bass frequencies (lower frequency bands)
        updateStars(width, height);
        
        // Draw stars
        for (Star star : stars) {
            double alpha = Math.max(0.1, Math.min(1.0, star.brightness));
            gc.setFill(Color.hsb(star.hue, 0.8, alpha));
            
            // Draw star with size based on brightness
            double size = star.size * star.brightness;
            gc.fillOval(star.x - size/2, star.y - size/2, size, size);
            
            // Draw star trails for brighter stars
            if (star.brightness > 0.5) {
                gc.setStroke(Color.hsb(star.hue, 0.8, alpha * 0.3));
                gc.setLineWidth(1);
                gc.strokeLine(star.x, star.y, star.x - star.vx * 5, star.y - star.vy * 5);
            }
        }
        
        // Draw constellation lines between bright stars
        List<Star> brightStars = stars.stream()
            .filter(s -> s.brightness > 0.7)
            .collect(ArrayList::new, (list, star) -> list.add(star), ArrayList::addAll);
            
        gc.setStroke(Color.gray(0.3));
        gc.setLineWidth(0.5);
        for (int i = 0; i < brightStars.size() - 1; i++) {
            Star s1 = brightStars.get(i);
            for (int j = i + 1; j < brightStars.size(); j++) {
                Star s2 = brightStars.get(j);
                double distance = Math.sqrt(Math.pow(s1.x - s2.x, 2) + Math.pow(s1.y - s2.y, 2));
                
                if (distance < 150) { // Only connect nearby stars
                    gc.strokeLine(s1.x, s1.y, s2.x, s2.y);
                }
            }
        }
    }
    
    /**
     * Updates the waveform history buffer with current audio data.
     */
    private void updateWaveformHistory() {
        // Copy current smoothed magnitudes to history for consistent scaling
        if (smoothedMagnitudes != null && smoothedMagnitudes.length == OUTPUT_BANDS) {
            System.arraycopy(smoothedMagnitudes, 0, waveformHistory[waveformHistoryIndex], 0, OUTPUT_BANDS);
            waveformHistoryIndex = (waveformHistoryIndex + 1) % waveformHistory.length;
        }
    }
    
    /**
     * Updates stars in the star field based on bass frequencies.
     */
    private void updateStars(double width, double height) {
        // Calculate bass energy (use first few frequency bands)
        double bassEnergy = 0;
        int bassBands = Math.min(8, magnitudes.length);
        for (int i = 0; i < bassBands; i++) {
            bassEnergy += Math.max(0, magnitudes[i] + 60); // Normalize from -60dB
        }
        bassEnergy /= bassBands;
        
        // Create stars if we don't have enough
        while (stars.size() < 200) {
            stars.add(new Star(
                starRandom.nextDouble() * width,
                starRandom.nextDouble() * height,
                starRandom.nextDouble() * 360, // hue
                2 + starRandom.nextDouble() * 4 // size
            ));
        }
        
        // Update existing stars
        for (Star star : stars) {
            star.update(bassEnergy, width, height);
        }
        
        // Remove dim stars and add new ones occasionally  
        stars.removeIf(star -> star.brightness < 0.1);
        
        // Occasionally add new stars during high bass
        if (bassEnergy > 20 && starRandom.nextDouble() < 0.1) {
            stars.add(new Star(
                starRandom.nextDouble() * width,
                starRandom.nextDouble() * height,
                starRandom.nextDouble() * 360,
                2 + starRandom.nextDouble() * 6
            ));
        }
    }

    /**
     * Handles media-related errors by displaying user-friendly dialog.
     * 
     * @param message Error message to display
     * @param e Exception that occurred
     */
    private void handleMediaError(String message, Exception e) {
        Platform.runLater(() -> {
            showErrorDialog("Media Error", message + "\n\nError: " + e.getMessage());
        });
    }

    /**
     * Displays an error dialog to the user.
     * 
     * @param title Dialog title
     * @param message Error message to display
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an information dialog to the user.
     * 
     * @param title Dialog title
     * @param message Information message to display
     */
    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * JavaFX Application stop method. Performs cleanup when application exits.
     * 
     * @throws Exception if cleanup fails
     */
    @Override
    public void stop() throws Exception {
        cleanup();
        super.stop();
    }

    /**
     * Performs cleanup of resources including MediaPlayer and AnimationTimer.
     * Handles exceptions gracefully to prevent exit errors.
     */
    private void cleanup() {
        // Prevent double cleanup
        if (isCleanedUp) {
            System.out.println("[CLEANUP] Cleanup already performed, skipping");
            return;
        }
        
        System.out.println("[CLEANUP] Starting application cleanup");
        isCleanedUp = true;
        
        if (animationTimer != null) {
            System.out.println("[CLEANUP] Stopping animation timer");
            try {
                animationTimer.stop();
                System.out.println("[CLEANUP] Animation timer stopped");
            } catch (Exception e) {
                System.err.println("[ERROR] Error stopping animation timer: " + e.getMessage());
                e.printStackTrace();
            }
            animationTimer = null;
        } else {
            System.out.println("[CLEANUP] Animation timer was null");
        }
        
        if (mediaPlayer != null) {
            System.out.println("[CLEANUP] Stopping and disposing MediaPlayer");
            try {
                MediaPlayer.Status status = mediaPlayer.getStatus();
                System.out.println("[CLEANUP] MediaPlayer status before cleanup: " + status);
                
                // Only stop if not already stopped/disposed
                if (status != MediaPlayer.Status.STOPPED && 
                    status != MediaPlayer.Status.DISPOSED &&
                    status != MediaPlayer.Status.HALTED) {
                    mediaPlayer.stop();
                    System.out.println("[CLEANUP] MediaPlayer stopped");
                } else {
                    System.out.println("[CLEANUP] MediaPlayer already stopped/disposed, skipping stop()");
                }
                
                // Always try to dispose, but catch any errors
                mediaPlayer.dispose();
                System.out.println("[CLEANUP] MediaPlayer disposed");
                
            } catch (Exception e) {
                System.err.println("[ERROR] Error during MediaPlayer cleanup: " + e.getMessage());
                // Don't print full stack trace for expected disposal errors
                if (!e.getMessage().contains("jfxPlayer") || !e.getMessage().contains("null")) {
                    e.printStackTrace();
                }
            }
            mediaPlayer = null;
        } else {
            System.out.println("[CLEANUP] MediaPlayer was null");
        }
        
        System.out.println("[CLEANUP] Application cleanup completed");
    }

    /**
     * Inner class representing a particle in the particle system.
     * Each particle has position, velocity, size, lifetime, and color properties.
     */
    class Particle {
        /** Particle position coordinates */
        double x, y;
        /** Current particle size */
        double size;
        /** Particle velocity components */
        double vx, vy;
        /** Remaining lifetime in frames */
        double lifetime;
        /** Particle hue for HSB color calculation */
        double hue;

        /**
         * Creates a new particle with random velocity and properties.
         * 
         * @param x Initial x position
         * @param y Initial y position
         * @param size Initial particle size
         */
        Particle(double x, double y, double size) {
            this.x = x;
            this.y = y;
            this.size = size;
            // Random velocity in range [-5, 5] for both x and y
            this.vx = (random.nextDouble() - 0.5) * 10;
            this.vy = (random.nextDouble() - 0.5) * 10;
            this.lifetime = PARTICLE_LIFETIME_FRAMES;
            // Random hue for color variety
            this.hue = random.nextDouble() * 360;
        }

        /**
         * Updates particle position, size, and lifetime for next frame.
         * Applies velocity to position and decay to size and lifetime.
         */
        void update() {
            // Update position based on velocity
            x += vx;
            y += vy;
            // Apply size decay for shrinking effect
            size *= PARTICLE_SIZE_DECAY;
            // Decrease lifetime (faster decay = 2 frames per update)
            lifetime -= 2;
        }
    }
    
    /**
     * Inner class representing a star in the star field visualization.
     * Each star responds to bass frequencies and moves through space.
     */
    class Star {
        /** Star position coordinates */
        double x, y;
        /** Star movement velocity */
        double vx, vy;
        /** Star color hue */
        double hue;
        /** Star size */
        double size;
        /** Star brightness (0.0 to 1.0) */
        double brightness;
        /** Star lifetime for fading effects */
        double lifetime;
        
        /**
         * Creates a new star with specified properties.
         * 
         * @param x Initial x position
         * @param y Initial y position  
         * @param hue Color hue (0-360)
         * @param size Star size
         */
        Star(double x, double y, double hue, double size) {
            this.x = x;
            this.y = y;
            this.hue = hue;
            this.size = size;
            this.brightness = 0.3 + starRandom.nextDouble() * 0.7;
            this.lifetime = 100 + starRandom.nextDouble() * 200;
            
            // Random initial velocity
            this.vx = (starRandom.nextDouble() - 0.5) * 2;
            this.vy = (starRandom.nextDouble() - 0.5) * 2;
        }
        
        /**
         * Updates star position, brightness, and lifetime based on bass energy.
         * 
         * @param bassEnergy Current bass frequency energy
         * @param width Canvas width for boundary checking
         * @param height Canvas height for boundary checking
         */
        void update(double bassEnergy, double width, double height) {
            // Update position
            x += vx;
            y += vy;
            
            // Respond to bass energy
            if (bassEnergy > 15) {
                brightness = Math.min(1.0, brightness + 0.1);
                
                // Move faster during high bass
                vx *= 1.1;
                vy *= 1.1;
                
                // Change hue slightly
                hue = (hue + 1) % 360;
            } else {
                brightness *= 0.995; // Gradual fade
            }
            
            // Wrap around screen edges
            if (x < 0) x = width;
            if (x > width) x = 0;
            if (y < 0) y = height;
            if (y > height) y = 0;
            
            // Limit velocity
            double maxVel = 5;
            if (Math.abs(vx) > maxVel) vx = Math.signum(vx) * maxVel;
            if (Math.abs(vy) > maxVel) vy = Math.signum(vy) * maxVel;
            
            // Update lifetime
            lifetime -= 1;
            if (lifetime <= 0) {
                brightness *= 0.9; // Fade out when lifetime expires
            }
        }
    }
}