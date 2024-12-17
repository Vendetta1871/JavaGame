#version 330 core
in vec2 fragTexCoord;

out vec4 finalColor;

uniform sampler2D AccumTexture;
uniform sampler2D RevealageTexture;
uniform sampler2D OpaqueTexture;

void main() {
    vec4 accumColor = texture(AccumTexture, fragTexCoord);
    float revelage = texture(RevealageTexture, fragTexCoord).r;
    vec3 opaqueColor = texture(OpaqueTexture, fragTexCoord).rgb;
    vec3 color = accumColor.rgb / max(accumColor.a, 0.0001) * (1 - revelage) + opaqueColor * revelage;
    finalColor = vec4(color, 1 - revelage);
}
