#version 330 core

in vec2 uvOut;
in float outLight;

layout (location = 1) out float Revealage;

uniform sampler2D textureSampler;

void main() {
    vec4 fragColor = texture(textureSampler, uvOut);
    Revealage = 1.0 - fragColor.a;
}
