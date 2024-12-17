#version 330 core

in vec2 uvOut;
in float outLight;

layout (location = 0) out vec4 AccumColor;

uniform sampler2D textureSampler;

void main() {
    vec4 fragColor = texture(textureSampler, uvOut);
    float weight = fragColor.a;
    AccumColor = vec4(outLight * fragColor.rgb * weight, weight);
}
