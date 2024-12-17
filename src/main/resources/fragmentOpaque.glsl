#version 330 core

in vec2 outTexCoord;
in float outLight;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main()
{
    vec4 color = texture(textureSampler, outTexCoord);
    if (color.w < 0.01) discard; // draw completely transparent parts of custom textures
    fragColor = vec4(outLight * color.xyz, color.w);
}
