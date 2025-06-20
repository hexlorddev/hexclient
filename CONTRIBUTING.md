# Contributing to HexClient

We welcome contributions to HexClient! This document provides guidelines for contributing to the project.

## Development Setup

### Prerequisites
- JDK 21 or higher
- Git
- IntelliJ IDEA or Eclipse (recommended)

### Setting Up the Development Environment

1. **Clone the repository**
   ```bash
   git clone https://github.com/hexclient/hexclient.git
   cd hexclient
   ```

2. **Import the project**
   - IntelliJ IDEA: Open the `build.gradle` file
   - Eclipse: Import as Gradle project

3. **Generate development workspace**
   ```bash
   ./gradlew genEclipseRuns  # For Eclipse
   ./gradlew genIntellijRuns # For IntelliJ
   ```

## Code Style

### Java Conventions
- Use 4 spaces for indentation
- Follow Oracle Java naming conventions
- Add JavaDoc comments for public methods and classes
- Keep lines under 120 characters when possible

### Project Structure
```
src/main/java/com/hexclient/
├── core/          # Core client functionality
├── features/      # Feature implementations
│   └── modules/   # Individual feature modules
├── gui/           # User interface components
├── mixins/        # Mixin classes for Minecraft integration
├── utils/         # Utility classes
└── config/        # Configuration management
```

## Adding New Features

### Creating a New Feature Module

1. **Create the feature class**
   ```java
   public class YourFeature extends Feature {
       public YourFeature() {
           super("YourFeature", "Description", FeatureCategory.CATEGORY);
       }
       
       @Override
       public void onTick() {
           // Feature logic here
       }
   }
   ```

2. **Register the feature**
   Add your feature to `FeatureManager.initializeFeatures()`

3. **Add configuration options**
   Update the configuration system if needed

### Adding Mixins

1. **Create the mixin class**
   ```java
   @Mixin(TargetClass.class)
   public class TargetClassMixin {
       @Inject(method = "methodName", at = @At("HEAD"))
       private void onMethodName(CallbackInfo ci) {
           // Mixin logic here
       }
   }
   ```

2. **Register the mixin**
   Add it to `hexclient.mixins.json`

## Testing

### Manual Testing
- Test your changes in a development environment
- Verify compatibility with different Minecraft versions
- Test on different server types (vanilla, modded, etc.)

### Automated Testing
- Add unit tests for utility functions
- Test configuration loading/saving
- Verify feature enable/disable functionality

## Pull Request Process

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**
   - Follow the code style guidelines
   - Add appropriate documentation
   - Test thoroughly

4. **Commit your changes**
   ```bash
   git commit -am "Add feature: description of your feature"
   ```

5. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create a Pull Request**
   - Provide a clear description of the changes
   - Reference any related issues
   - Include screenshots/GIFs for UI changes

## Pull Request Guidelines

### Title Format
- Use descriptive titles
- Start with the type of change (Add, Fix, Update, etc.)
- Example: "Add: New AutoFarm feature for automated farming"

### Description Requirements
- Describe what the PR does
- Explain why the change is needed
- List any breaking changes
- Include testing information

### Code Review Process
1. At least one maintainer must review the PR
2. All CI checks must pass
3. No merge conflicts
4. Documentation must be updated if needed

## Issue Reporting

### Bug Reports
Include the following information:
- HexClient version
- Minecraft version
- Fabric Loader version
- Steps to reproduce
- Expected vs actual behavior
- Crash logs (if applicable)

### Feature Requests
- Clearly describe the requested feature
- Explain the use case and benefits
- Consider implementation complexity
- Check if similar features already exist

## Code of Conduct

### Be Respectful
- Use welcoming and inclusive language
- Respect differing viewpoints and experiences
- Accept constructive criticism gracefully

### Be Professional
- Focus on technical merit of contributions
- Avoid personal attacks or harassment
- Keep discussions on-topic

## License

By contributing to HexClient, you agree that your contributions will be licensed under the MIT License.

## Getting Help

- **Discord**: Join our Discord server for real-time help
- **GitHub Issues**: Create an issue for bugs or feature requests
- **Documentation**: Check the README and code comments

Thank you for contributing to HexClient!